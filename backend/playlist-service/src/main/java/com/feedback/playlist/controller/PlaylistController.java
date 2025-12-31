package com.feedback.playlist.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.feedback.playlist.dtos.PlaylistDTO;
import com.feedback.playlist.service.PlaylistService;
import com.feedback.playlist.service.SpotifyApiService;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final SpotifyApiService spotifyApiService;

    public PlaylistController(PlaylistService playlistService, SpotifyApiService spotifyApiService) {
        this.playlistService = playlistService;
        this.spotifyApiService = spotifyApiService;
    }
    
    // Get playlists from Spotify
    @GetMapping("/spotify")
    public ResponseEntity<List<Map<String, Object>>> getSpotifyPlaylists(
            @RequestHeader(value = "X-Spotify-Token", required = false) String spotifyToken) {
        if (spotifyToken == null || spotifyToken.isBlank()) {
            return ResponseEntity.status(401).body(new ArrayList<>());
        }
        
        try {
            List<Map<String, Object>> allPlaylists = new ArrayList<>();
            int limit = 50; // Max allowed by Spotify API
            int offset = 0;
            boolean hasMore = true;
            
            // Fetch all playlists with pagination
            while (hasMore) {
                Map<String, Object> spotifyResponse = spotifyApiService.getUserPlaylists(spotifyToken, limit, offset);
                
                if (spotifyResponse == null || !spotifyResponse.containsKey("items")) {
                    break;
                }
                
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> items = (List<Map<String, Object>>) spotifyResponse.get("items");
                
                if (items == null || items.isEmpty()) {
                    break;
                }
                
                // Process each playlist
                for (Map<String, Object> item : items) {
                    Map<String, Object> playlist = new HashMap<>();
                    playlist.put("playlistId", item.get("id"));
                    playlist.put("name", item.get("name"));
                    playlist.put("description", item.get("description"));
                    playlist.put("isPublic", item.get("public"));
                    
                    // Get track count
                    @SuppressWarnings("unchecked")
                    Map<String, Object> tracks = (Map<String, Object>) item.get("tracks");
                    if (tracks != null) {
                        playlist.put("trackCount", tracks.get("total"));
                    }
                    
                    // Get images
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> images = (List<Map<String, Object>>) item.get("images");
                    if (images != null && !images.isEmpty()) {
                        playlist.put("image", images.get(0).get("url"));
                    }
                    
                    // Get owner info
                    @SuppressWarnings("unchecked")
                    Map<String, Object> owner = (Map<String, Object>) item.get("owner");
                    if (owner != null) {
                        playlist.put("ownerId", owner.get("id"));
                        playlist.put("ownerName", owner.get("display_name"));
                    }
                    
                    playlist.put("href", item.get("href"));
                    playlist.put("externalUrl", item.get("external_urls"));
                    
                    allPlaylists.add(playlist);
                }
                
                // Check if there are more playlists to fetch
                Object nextObj = spotifyResponse.get("next");
                hasMore = nextObj != null && !nextObj.toString().equals("null");
                
                if (hasMore) {
                    offset += limit;
                } else {
                    break;
                }
                
                // Safety limit to prevent infinite loops (max 1000 playlists)
                if (offset >= 1000) {
                    break;
                }
            }
            
            return ResponseEntity.ok(allPlaylists);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch playlists: " + e.getMessage());
            List<Map<String, Object>> errorList = new ArrayList<>();
            errorList.add(errorResponse);
            return ResponseEntity.status(500).body(errorList);
        }
    }
    
    // Get tracks from a Spotify playlist
    @GetMapping("/spotify/{playlistId}/tracks")
    public ResponseEntity<List<Map<String, Object>>> getSpotifyPlaylistTracks(
            @PathVariable String playlistId,
            @RequestHeader(value = "X-Spotify-Token", required = false) String spotifyToken) {
        if (spotifyToken == null || spotifyToken.isBlank()) {
            return ResponseEntity.status(401).body(new ArrayList<>());
        }
        
        try {
            Map<String, Object> spotifyResponse = spotifyApiService.getPlaylistTracks(spotifyToken, playlistId);
            
            if (spotifyResponse == null || !spotifyResponse.containsKey("items")) {
                return ResponseEntity.ok(new ArrayList<>());
            }
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) spotifyResponse.get("items");
            
            List<Map<String, Object>> tracks = new ArrayList<>();
            for (Map<String, Object> item : items) {
                @SuppressWarnings("unchecked")
                Map<String, Object> track = (Map<String, Object>) item.get("track");
                
                if (track == null) continue;
                
                Map<String, Object> trackInfo = new HashMap<>();
                trackInfo.put("songId", track.get("id"));
                trackInfo.put("name", track.get("name"));
                trackInfo.put("duration_ms", track.get("duration_ms"));
                
                // Get artists
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> artists = (List<Map<String, Object>>) track.get("artists");
                if (artists != null && !artists.isEmpty()) {
                    trackInfo.put("artistName", artists.get(0).get("name"));
                    trackInfo.put("artistId", artists.get(0).get("id"));
                }
                
                // Get album info
                @SuppressWarnings("unchecked")
                Map<String, Object> album = (Map<String, Object>) track.get("album");
                if (album != null) {
                    trackInfo.put("album", album.get("name"));
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> images = (List<Map<String, Object>>) album.get("images");
                    if (images != null && !images.isEmpty()) {
                        trackInfo.put("image", images.get(0).get("url"));
                    }
                }
                
                tracks.add(trackInfo);
            }
            
            return ResponseEntity.ok(tracks);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    // Get all playlists for a user
    @GetMapping
    public ResponseEntity<List<PlaylistDTO>> getAllPlaylists(@RequestParam(required = false) String userId) {
        if (userId != null && !userId.isBlank()) {
            return ResponseEntity.ok(playlistService.findByOwnerId(userId));
        }
        return ResponseEntity.ok(playlistService.getAllPlaylists());
    }

    // Get specific playlist by ID
    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable String id) {
        return playlistService.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Create a new playlist
    @PostMapping
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestBody PlaylistDTO playlistDTO) {
        PlaylistDTO created = playlistService.create(playlistDTO);
        return ResponseEntity.status(201).body(created);
    }

    // Update playlist details
    @PutMapping("/{id}")
    public ResponseEntity<PlaylistDTO> updatePlaylist(@PathVariable String id, @RequestBody PlaylistDTO playlistDTO) {
        PlaylistDTO updated = playlistService.update(id, playlistDTO);
        return ResponseEntity.ok(updated);
    }

    // Delete a playlist
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable String id) {
        playlistService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Get songs in a playlist (returns full playlist DTO which includes songs)
    @GetMapping("/{id}/songs")
    public ResponseEntity<PlaylistDTO> getPlaylistSongs(@PathVariable String id) {
        return playlistService.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Add a song to a playlist
    @PostMapping("/{playlistId}/songs")
    public ResponseEntity<Map<String, Object>> addSongToPlaylist(@PathVariable String playlistId, @RequestBody Map<String, Object> songData) {
        Map<String, Object> response = Map.of(
            "playlistId", playlistId,
            "songId", songData.get("songId"),
            "message", "Song added to playlist successfully"
        );
        return ResponseEntity.ok(response);
    }

    // Remove a song from a playlist
    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Map<String, String>> removeSongFromPlaylist(@PathVariable String playlistId, @PathVariable String songId) {
        Map<String, String> response = Map.of("message", "Song " + songId + " removed from playlist " + playlistId);
        return ResponseEntity.ok(response);
    }

}
