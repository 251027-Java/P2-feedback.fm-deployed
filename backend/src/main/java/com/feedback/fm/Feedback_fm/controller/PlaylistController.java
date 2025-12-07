package com.feedback.fm.Feedback_fm.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
@CrossOrigin(origins = "http://localhost:3000")
public class PlaylistController {

    // Get all playlists for a user
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllPlaylists(@RequestParam Long userId) {
        List<Map<String, Object>> playlists = new ArrayList<>();    
        return ResponseEntity.ok(playlists);
    }

    // Get specific playlist by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPlaylistById(@PathVariable Long id) {
        return null;
    }

    // Create a new playlist
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPlaylist(@RequestBody Map<String, Object> playlistData) {
        return null;
    }

    // Update playlist details
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePlaylist( @PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return null;
    }

    // Delete a playlist
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePlaylist(@PathVariable Long id) {
        return null;
    }

    // Get songs in a playlist
    @GetMapping("/{id}/songs")
    public ResponseEntity<List<Map<String, Object>>> getPlaylistSongs(@PathVariable Long id) {
        return null;
    }

    // Add a song to a playlist
    @PostMapping("/{playlistId}/songs")
    public ResponseEntity<Map<String, Object>> addSongToPlaylist(@PathVariable Long playlistId, @RequestBody Map<String, Object> songData) {
        return null;
    }

    // Remove a song from a playlist
    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Map<String, String>> removeSongFromPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        return null;
    }

}
