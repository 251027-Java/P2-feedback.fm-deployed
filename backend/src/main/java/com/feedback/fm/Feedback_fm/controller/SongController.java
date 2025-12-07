package com.feedback.fm.Feedback_fm.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "http://localhost:3000")
public class SongController {

	// Get all songs with pagination
	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> getAllSongs( @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String query) {
		List<Map<String, Object>> songs = List.of(
			Map.of("songId", 1L, "title", "Come Together", "artist", "The Beatles", "duration", 259),
			Map.of("songId", 2L, "title", "Get Lucky", "artist", "Daft Punk", "duration", 248)
		);
		return ResponseEntity.ok(songs);
	}

	// Get specific song by ID
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getSongById(@PathVariable Long id) {
		Map<String, Object> song = Map.of(
			"songId", id,
			"title", "Come Together",
			"artist", "The Beatles",
			"album", "Abbey Road",
			"duration", 259
		);
		return ResponseEntity.ok(song);
	}

	// Create a new song (admin only)
	@PostMapping
	public ResponseEntity<Map<String, Object>> createSong(@RequestBody Map<String, Object> songData) {
		Map<String, Object> newSong = Map.of(
			"songId", 999L,
			"title", songData.getOrDefault("title", "Untitled"),
			"artist", songData.getOrDefault("artist", "Unknown"),
			"message", "Song created successfully"
		);
		return ResponseEntity.status(201).body(newSong);
	}

	// Update song details (admin only)
	@PutMapping("/{id}")
	public ResponseEntity<Map<String, Object>> updateSong(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
		Map<String, Object> updatedSong = Map.of(
			"songId", id,
			"title", updates.getOrDefault("title", "Updated Song"),
			"message", "Song updated successfully"
		);
		return ResponseEntity.ok(updatedSong);
	}

	// Delete a song (admin only)
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteSong(@PathVariable Long id) {
		Map<String, String> response = Map.of("message", "Song " + id + " deleted successfully");
		return ResponseEntity.ok(response);
	}

	// Get songs by artist
	@GetMapping("/by-artist/{artistId}")
	public ResponseEntity<List<Map<String, Object>>> getSongsByArtist(@PathVariable Long artistId) {
		List<Map<String, Object>> songs = List.of(
			Map.of("songId", 1L, "title", "Come Together", "duration", 259),
			Map.of("songId", 3L, "title", "Something", "duration", 182)
		);
		return ResponseEntity.ok(songs);
	}

	// Get songs by album
	@GetMapping("/by-album/{albumId}")
	public ResponseEntity<List<Map<String, Object>>> getSongsByAlbum(@PathVariable Long albumId) {
		List<Map<String, Object>> songs = List.of(
			Map.of("songId", 1L, "title", "Come Together", "trackNumber", 1),
			Map.of("songId", 2L, "title", "Something", "trackNumber", 2)
		);
		return ResponseEntity.ok(songs);
	}

	// Search for songs
	@GetMapping("/search")
	public ResponseEntity<List<Map<String, Object>>> searchSongs(@RequestParam String query) {
		List<Map<String, Object>> results = List.of(
			Map.of("songId", 1L, "title", "Come Together", "artist", "The Beatles"),
			Map.of("songId", 5L, "title", "Come As You Are", "artist", "Nirvana")
		);
		return ResponseEntity.ok(results);
	}

	// Like a song
	@PostMapping("/{id}/like")
	public ResponseEntity<Map<String, String>> likeSong(@PathVariable Long id, @RequestParam Long userId) {
		Map<String, String> response = Map.of("message", "Song " + id + " liked by user " + userId);
		return ResponseEntity.ok(response);
	}

	// Unlike a song
	@DeleteMapping("/{id}/like")
	public ResponseEntity<Map<String, String>> unlikeSong(@PathVariable Long id, @RequestParam Long userId) {
		Map<String, String> response = Map.of("message", "Song " + id + " unliked by user " + userId);
		return ResponseEntity.ok(response);
	}
}
