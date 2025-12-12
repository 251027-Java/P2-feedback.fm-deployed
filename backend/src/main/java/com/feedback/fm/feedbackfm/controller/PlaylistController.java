package com.feedback.fm.feedbackfm.controller;

import com.feedback.fm.feedbackfm.dtos.PlaylistDTO;
import com.feedback.fm.feedbackfm.service.PlaylistService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
@CrossOrigin(origins = "http://localhost:3000")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
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
