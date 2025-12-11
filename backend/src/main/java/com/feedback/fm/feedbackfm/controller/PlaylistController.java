package com.feedback.fm.feedbackfm.controller;

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
    public ResponseEntity<List<Map<String, Object>>> getAllPlaylists(@RequestParam String userId) {
        List<Map<String, Object>> playlists = List.of(
            Map.of("playlistId", "playlist1", "name", "Favorites", "ownerId", userId, "isPublic", true, "href", "https://open.spotify.com/playlist/example1"),
            Map.of("playlistId", "playlist2", "name", "Workout Mix", "ownerId", userId, "isPublic", false, "href", "https://open.spotify.com/playlist/example2")
        );
        return ResponseEntity.ok(playlists);
    }

    // Get specific playlist by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPlaylistById(@PathVariable String id) {
        Map<String, Object> playlist = Map.of(
            "playlistId", id,
            "name", "Favorites",
            "description", "My favorite songs",
            "isPublic", true,
            "href", "https://open.spotify.com/playlist/example1"
        );
        return ResponseEntity.ok(playlist);
    }

    // Create a new playlist
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPlaylist(@RequestBody Map<String, Object> playlistData) {
        Map<String, Object> newPlaylist = Map.of(
            "playlistId", "new-playlist-id",
            "name", playlistData.getOrDefault("name", "New Playlist"),
            "description", playlistData.getOrDefault("description", ""),
            "isPublic", playlistData.getOrDefault("isPublic", false),
            "message", "Playlist created successfully"
        );
        return ResponseEntity.status(201).body(newPlaylist);
    }

    // Update playlist details
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePlaylist( @PathVariable String id, @RequestBody Map<String, Object> updates) {
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
    public ResponseEntity<Map<String, String>> deletePlaylist(@PathVariable String id) {
        Map<String, String> response = Map.of("message", "Playlist " + id + " deleted successfully");
        return ResponseEntity.ok(response);
    }

    // Get songs in a playlist
    @GetMapping("/{id}/songs")
    public ResponseEntity<List<Map<String, Object>>> getPlaylistSongs(@PathVariable String id) {
        List<Map<String, Object>> songs = List.of(
            Map.of("songId", "song1", "name", "Come Together", "durationMs", 259000),
            Map.of("songId", "song2", "name", "Get Lucky", "durationMs", 248000)
        );
        return ResponseEntity.ok(songs);
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
