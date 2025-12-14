package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.AlbumDTO;
import com.feedback.fm.feedbackfm.dtos.ArtistDTO;
import com.feedback.fm.feedbackfm.dtos.HistoryDTO;
import com.feedback.fm.feedbackfm.dtos.ListenerDTO;
import com.feedback.fm.feedbackfm.dtos.PlaylistDTO;
import com.feedback.fm.feedbackfm.dtos.SongDTO;
import com.feedback.fm.feedbackfm.model.Album;
import com.feedback.fm.feedbackfm.model.Artist;
import com.feedback.fm.feedbackfm.model.Listener;
import com.feedback.fm.feedbackfm.model.Song;
import com.feedback.fm.feedbackfm.repository.AlbumRepository;
import com.feedback.fm.feedbackfm.repository.ArtistRepository;
import com.feedback.fm.feedbackfm.repository.ListenerRepository;
import com.feedback.fm.feedbackfm.repository.SongRepository;
import com.feedback.fm.feedbackfm.service.spotify.SpotifyApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpotifySyncService {
    
    private final SpotifyApiService spotifyApiService;
    private final ListenerService listenerService;
    private final SongService songService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final PlaylistService playlistService;
    private final HistoryService historyService;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final ListenerRepository listenerRepository;
    
    @Autowired
    public SpotifySyncService(
            SpotifyApiService spotifyApiService,
            ListenerService listenerService,
            SongService songService,
            ArtistService artistService,
            AlbumService albumService,
            PlaylistService playlistService,
            HistoryService historyService,
            ArtistRepository artistRepository,
            SongRepository songRepository,
            AlbumRepository albumRepository,
            ListenerRepository listenerRepository) {
        this.spotifyApiService = spotifyApiService;
        this.listenerService = listenerService;
        this.songService = songService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.playlistService = playlistService;
        this.historyService = historyService;
        this.artistRepository = artistRepository;
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.listenerRepository = listenerRepository;
    }
    
    public void syncUserProfile(String accessToken) {
        Map<String, Object> spotifyUser = spotifyApiService.getCurrentUser(accessToken);
        
        String spotifyId = (String) spotifyUser.get("id");
        String displayName = (String) spotifyUser.get("display_name");
        String email = (String) spotifyUser.get("email");
        String country = (String) spotifyUser.get("country");
        Map<String, Object> externalUrls = (Map<String, Object>) spotifyUser.get("external_urls");
        String href = externalUrls != null ? (String) externalUrls.get("spotify") : null;
        
        ListenerDTO listenerDTO = new ListenerDTO(spotifyId, displayName, email, country, href);
        
        var existingListener = listenerService.getById(spotifyId);
        if (existingListener.isEmpty()) {
            listenerService.create(listenerDTO);
        } else {
            listenerService.update(spotifyId, listenerDTO);
        }
    }
    
    public void syncRecentlyPlayed(String accessToken, String listenerId) {
        Map<String, Object> recentlyPlayed = spotifyApiService.getRecentlyPlayed(accessToken, 50);
        
        if (recentlyPlayed == null || !recentlyPlayed.containsKey("items")) {
            return;
        }
        
        List<Map<String, Object>> items = (List<Map<String, Object>>) recentlyPlayed.get("items");
        
        // Get listener entity to update cumulative stats
        com.feedback.fm.feedbackfm.model.Listener listener = listenerRepository.findById(listenerId).orElse(null);
        if (listener == null) {
            System.err.println("Listener not found: " + listenerId);
            return;
        }
        
        long newListeningTime = 0L;
        int newSongsCount = 0;
        
        for (Map<String, Object> item : items) {
            Map<String, Object> track = (Map<String, Object>) item.get("track");
            if (track == null) continue;
            
            // Sync song, artists, and album first
            String songId = syncSong(track);
            syncArtistsForSong(track, songId);
            syncAlbumForSong(track, songId);
            
            // Create history entry
            String playedAtStr = (String) item.get("played_at");
            LocalDateTime playedAt = parseSpotifyTimestamp(playedAtStr);
            
            // Check if this history entry already exists (to avoid duplicates)
            List<com.feedback.fm.feedbackfm.dtos.HistoryDTO> existingHistory = 
                historyService.findByListenerIdAndSongId(listenerId, songId);
            
            // Check if we've already recorded this specific play (by timestamp within 1 minute tolerance)
            boolean alreadyRecorded = false;
            for (com.feedback.fm.feedbackfm.dtos.HistoryDTO hist : existingHistory) {
                if (hist.playedAt() != null && 
                    java.time.Duration.between(hist.playedAt(), playedAt).abs().toMinutes() < 1) {
                    alreadyRecorded = true;
                    break;
                }
            }
            
            if (!alreadyRecorded) {
                HistoryDTO historyDTO = new HistoryDTO(null, playedAt, listenerId, songId);
                historyService.create(historyDTO);
                
                // Get song duration and add to cumulative stats
                Song song = songRepository.findById(songId).orElse(null);
                if (song != null && song.getDurationMs() != null) {
                    newListeningTime += song.getDurationMs();
                    newSongsCount++;
                }
            }
        }
        
        // Update cumulative stats
        if (newSongsCount > 0 || newListeningTime > 0) {
            long currentTotalTime = listener.getTotalListeningTimeMs() != null ? listener.getTotalListeningTimeMs() : 0L;
            int currentSongsPlayed = listener.getTotalSongsPlayed() != null ? listener.getTotalSongsPlayed() : 0;
            
            listener.setTotalListeningTimeMs(currentTotalTime + newListeningTime);
            listener.setTotalSongsPlayed(currentSongsPlayed + newSongsCount);
            listenerRepository.save(listener);
            
            System.out.println("Updated stats for listener " + listenerId + 
                ": +" + newSongsCount + " songs, +" + (newListeningTime / 60000) + " minutes");
        }
    }
    
    public void syncTopArtists(String accessToken, String listenerId, String timeRange) {
        Map<String, Object> topArtists = spotifyApiService.getTopArtists(accessToken, timeRange);
        
        if (topArtists == null || !topArtists.containsKey("items")) {
            return;
        }
        
        List<Map<String, Object>> items = (List<Map<String, Object>>) topArtists.get("items");
        
        for (Map<String, Object> artistData : items) {
            syncArtist(artistData);
        }
    }
    
    public void syncTopTracks(String accessToken, String listenerId, String timeRange) {
        Map<String, Object> topTracks = spotifyApiService.getTopTracks(accessToken, timeRange);
        
        if (topTracks == null || !topTracks.containsKey("items")) {
            return;
        }
        
        List<Map<String, Object>> items = (List<Map<String, Object>>) topTracks.get("items");
        
        for (Map<String, Object> track : items) {
            String songId = syncSong(track);
            syncArtistsForSong(track, songId);
            syncAlbumForSong(track, songId);
        }
    }
    
    public void syncUserPlaylists(String accessToken, String listenerId) {
        Map<String, Object> playlistsResponse = spotifyApiService.getUserPlaylists(accessToken, 50, 0);
        
        if (playlistsResponse == null || !playlistsResponse.containsKey("items")) {
            return;
        }
        
        List<Map<String, Object>> items = (List<Map<String, Object>>) playlistsResponse.get("items");
        
        for (Map<String, Object> playlistData : items) {
            String playlistId = (String) playlistData.get("id");
            String name = (String) playlistData.get("name");
            String description = (String) playlistData.get("description");
            Map<String, Object> externalUrls = (Map<String, Object>) playlistData.get("external_urls");
            String href = externalUrls != null ? (String) externalUrls.get("spotify") : null;
            Boolean isPublic = (Boolean) playlistData.get("public");
            
            PlaylistDTO playlistDTO = new PlaylistDTO(
                playlistId,
                name,
                description,
                href,
                isPublic != null ? isPublic : false,
                listenerId,
                new ArrayList<>() // Songs will be synced separately if needed
            );
            
            var existingPlaylist = playlistService.getById(playlistId);
            if (existingPlaylist.isEmpty()) {
                playlistService.create(playlistDTO);
            } else {
                playlistService.update(playlistId, playlistDTO);
            }
        }
    }
    
    private String syncSong(Map<String, Object> track) {
        String songId = (String) track.get("id");
        String name = (String) track.get("name");
        Integer durationMs = (Integer) track.get("duration_ms");
        Map<String, Object> externalUrls = (Map<String, Object>) track.get("external_urls");
        String href = externalUrls != null ? (String) externalUrls.get("spotify") : null;
        
        SongDTO songDTO = new SongDTO(songId, name, href, durationMs, new ArrayList<>(), new ArrayList<>());
        
        var existingSong = songService.getById(songId);
        if (existingSong.isEmpty()) {
            songService.create(songDTO);
        } else {
            songService.update(songId, songDTO);
        }
        
        return songId;
    }
    
    private void syncArtistsForSong(Map<String, Object> track, String songId) {
        List<Map<String, Object>> artists = (List<Map<String, Object>>) track.get("artists");
        if (artists == null) return;
        
        Song song = songRepository.findById(songId).orElse(null);
        if (song == null) return;
        
        for (Map<String, Object> artistData : artists) {
            String artistId = (String) artistData.get("id");
            syncArtist(artistData);
            
            // Add artist to song if not already present
            Artist artist = artistRepository.findById(artistId).orElse(null);
            if (artist != null && !song.getArtists().contains(artist)) {
                song.getArtists().add(artist);
                artist.getSongs().add(song);
            }
        }
        songRepository.save(song);
    }
    
    private String syncArtist(Map<String, Object> artistData) {
        String artistId = (String) artistData.get("id");
        String name = (String) artistData.get("name");
        Map<String, Object> externalUrls = (Map<String, Object>) artistData.get("external_urls");
        String href = externalUrls != null ? (String) externalUrls.get("spotify") : null;
        
        ArtistDTO artistDTO = new ArtistDTO(artistId, name, href, new ArrayList<>());
        
        var existingArtist = artistService.getById(artistId);
        if (existingArtist.isEmpty()) {
            artistService.create(artistDTO);
        } else {
            artistService.update(artistId, artistDTO);
        }
        
        return artistId;
    }
    
    private void syncAlbumForSong(Map<String, Object> track, String songId) {
        Map<String, Object> albumData = (Map<String, Object>) track.get("album");
        if (albumData == null) return;
        
        String albumId = (String) albumData.get("id");
        String title = (String) albumData.get("name");
        
        // Parse release_date (Spotify API returns it as a string in format "YYYY", "YYYY-MM", or "YYYY-MM-DD")
        Integer releaseYear = null;
        Object releaseDateObj = albumData.get("release_date");
        if (releaseDateObj instanceof String) {
            String releaseDateStr = (String) releaseDateObj;
            if (releaseDateStr != null && releaseDateStr.length() >= 4) {
                try {
                    releaseYear = Integer.parseInt(releaseDateStr.substring(0, 4));
                } catch (NumberFormatException e) {
                    // Ignore parsing errors
                }
            }
        }
        
        Map<String, Object> externalUrls = (Map<String, Object>) albumData.get("external_urls");
        String href = externalUrls != null ? (String) externalUrls.get("spotify") : null;
        
        // Get primary artist for album
        List<Map<String, Object>> artists = (List<Map<String, Object>>) albumData.get("artists");
        String artistId = null;
        if (artists != null && !artists.isEmpty()) {
            artistId = (String) artists.get(0).get("id");
            syncArtist(artists.get(0));
        }
        
        AlbumDTO albumDTO = new AlbumDTO(albumId, title, releaseYear, href, artistId, new ArrayList<>());
        
        var existingAlbum = albumService.getById(albumId);
        if (existingAlbum.isEmpty()) {
            albumService.create(albumDTO);
        } else {
            albumService.update(albumId, albumDTO);
        }
        
        // Add song to album
        Song song = songRepository.findById(songId).orElse(null);
        Album album = albumRepository.findById(albumId).orElse(null);
        if (song != null && album != null && !song.getAlbums().contains(album)) {
            song.getAlbums().add(album);
            songRepository.save(song);
        }
    }
    
    /**
     * Update cumulative stats from a currently playing track
     * This should be called when checking currently playing to track new songs
     */
    public void updateStatsFromCurrentlyPlaying(String listenerId, Map<String, Object> trackData) {
        if (trackData == null) return;
        
        Listener listener = listenerRepository.findById(listenerId).orElse(null);
        if (listener == null) return;
        
        String songId = (String) trackData.get("id");
        if (songId == null) return;
        
        // Check if we've recently recorded this song (within last minute to avoid duplicates)
        List<com.feedback.fm.feedbackfm.dtos.HistoryDTO> recentHistory = 
            historyService.findByListenerIdAndSongId(listenerId, songId);
        
        LocalDateTime now = LocalDateTime.now();
        boolean recentlyRecorded = false;
        for (com.feedback.fm.feedbackfm.dtos.HistoryDTO hist : recentHistory) {
            if (hist.playedAt() != null && 
                java.time.Duration.between(hist.playedAt(), now).toMinutes() < 1) {
                recentlyRecorded = true;
                break;
            }
        }
        
        if (!recentlyRecorded) {
            // Sync the song first
            syncSong(trackData);
            
            // Create history entry for currently playing
            HistoryDTO historyDTO = new HistoryDTO(null, now, listenerId, songId);
            historyService.create(historyDTO);
            
            // Update cumulative stats
            Song song = songRepository.findById(songId).orElse(null);
            if (song != null && song.getDurationMs() != null) {
                long currentTotalTime = listener.getTotalListeningTimeMs() != null ? listener.getTotalListeningTimeMs() : 0L;
                int currentSongsPlayed = listener.getTotalSongsPlayed() != null ? listener.getTotalSongsPlayed() : 0;
                
                listener.setTotalListeningTimeMs(currentTotalTime + song.getDurationMs());
                listener.setTotalSongsPlayed(currentSongsPlayed + 1);
                listenerRepository.save(listener);
                
                System.out.println("Updated stats from currently playing: +1 song, +" + (song.getDurationMs() / 60000) + " minutes");
            }
        }
    }
    
    private LocalDateTime parseSpotifyTimestamp(String timestamp) {
        if (timestamp == null) {
            return LocalDateTime.now();
        }
        try {
            Instant instant = Instant.parse(timestamp);
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}

