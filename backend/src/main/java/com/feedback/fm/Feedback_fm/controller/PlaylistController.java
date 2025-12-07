package com.feedback.fm.Feedback_fm.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
@CrossOrigin(origins = "http://localhost:3000")
public class PlaylistController {

    // Get all playlists for a user
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllPlaylists(@RequestParam Long userId) {
        List<Map<String, Object>> playlists = List.of(
            Map.of("playlistId", 1L, "name", "Favorites", "songCount", 50, "isPublic", true),
            Map.of("playlistId", 2L, "name", "Workout Mix", "songCount", 35, "isPublic", false)
        );
        return ResponseEntity.ok(playlists);
    }

    // Get specific playlist by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPlaylistById(@PathVariable Long id) {
        Map<String, Object> playlist = Map.of(
            "playlistId", id,
            "name", "Favorites",
            "description", "My favorite songs",
            "songCount", 50,
            "isPublic", true
        );
        return ResponseEntity.ok(playlist);
    }

    // Create a new playlist
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPlaylist(@RequestBody Map<String, Object> playlistData) {
        Map<String, Object> newPlaylist = Map.of(
            "playlistId", 999L,
            "name", playlistData.getOrDefault("name", "New Playlist"),
            "description", playlistData.getOrDefault("description", ""),
            "isPublic", playlistData.getOrDefault("isPublic", false),
            "message", "Playlist created successfully"
        );
        return ResponseEntity.status(201).body(newPlaylist);
    }

    // Update playlist details
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePlaylist( @PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Map<String, Object> updatedPlaylist = Map.of(
            "playlistId", id,
            "name", updates.getOrDefault("name", "Updated Playlist"),
            "description", updates.getOrDefault("description", ""),
            "message", "Playlist updated successfully"
        );
        return ResponseEntity.ok(updatedPlaylist);
    }

    // Delete a playlist
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePlaylist(@PathVariable Long id) {
        Map<String, String> response = Map.of("message", "Playlist " + id + " deleted successfully");
        return ResponseEntity.ok(response);
    }

    // Get songs in a playlist
    @GetMapping("/{id}/songs")
    public ResponseEntity<List<Map<String, Object>>> getPlaylistSongs(@PathVariable Long id) {
        List<Map<String, Object>> songs = List.of(
            Map.of("songId", 1L, "title", "Come Together", "artist", "The Beatles", "duration", 259),
            Map.of("songId", 2L, "title", "Get Lucky", "artist", "Daft Punk", "duration", 248)
        );
        return ResponseEntity.ok(songs);
    }

    // Add a song to a playlist
    @PostMapping("/{playlistId}/songs")
    public ResponseEntity<Map<String, Object>> addSongToPlaylist(@PathVariable Long playlistId, @RequestBody Map<String, Object> songData) {
        Map<String, Object> response = Map.of(
            "playlistId", playlistId,
            "songId", songData.get("songId"),
            "message", "Song added to playlist successfully"
        );
        return ResponseEntity.ok(response);
    }

    // Remove a song from a playlist
    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Map<String, String>> removeSongFromPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        Map<String, String> response = Map.of("message", "Song " + songId + " removed from playlist " + playlistId);
        return ResponseEntity.ok(response);
    }

}
