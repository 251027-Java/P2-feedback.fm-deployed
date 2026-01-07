package com.feedback.spotify.service;

import com.feedback.spotify.dtos.ArtistDTO;
import com.feedback.spotify.dtos.SongDTO;
import com.feedback.spotify.model.Artist;
import com.feedback.spotify.model.Song;
import com.feedback.spotify.repository.AlbumRepository;
import com.feedback.spotify.repository.ArtistRepository;
import com.feedback.spotify.repository.SongRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SpotifySyncHelper {

    private final SongService songService;
    private final ArtistService artistService;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;

    public SpotifySyncHelper(SongService songService, ArtistService artistService, 
                            SongRepository songRepository, 
                            ArtistRepository artistRepository) {
        this.songService = songService;
        this.artistService = artistService;
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String syncSong(Map<String, Object> track) {
        String songId = (String) track.get("id");
        String name = (String) track.get("name");
        Integer durationMs = (Integer) track.get("duration_ms");
        Map<String, Object> externalUrls = (Map<String, Object>) track.get("external_urls");
        String href = externalUrls != null ? (String) externalUrls.get("spotify") : null;

        SongDTO songDTO = new SongDTO(songId, name, href, durationMs, new ArrayList<>(), new ArrayList<>());

        try {
            var fetchSongReq = songService.getSongById(songId);
            if (fetchSongReq.getStatusCode() == HttpStatus.OK) {
                songService.updateSong(songId, songDTO);
            } else {
                songService.createSong(songDTO);
            }
        } catch (Exception e) {
            // If Feign throws a 404 exception, we create the song
            songService.createSong(songDTO);
        }
        return songId;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void syncArtist(Map<String, Object> artistData) {
        String artistId = (String) artistData.get("id");
        String name = (String) artistData.get("name");
        Map<String, Object> externalUrls = (Map<String, Object>) artistData.get("external_urls");
        String href = externalUrls != null ? (String) externalUrls.get("spotify") : null;

        ArtistDTO artistDTO = new ArtistDTO(artistId, name, href, new ArrayList<>());

        try {
            var fetchArtistReq = artistService.getArtistById(artistId);
            if (fetchArtistReq.getStatusCode() != HttpStatus.OK) {
                artistService.createArtist(artistDTO);
            } else {
                artistService.updateArtist(artistId, artistDTO);
            }
        } catch (Exception e) {
            artistService.createArtist(artistDTO);
        }
    }
}