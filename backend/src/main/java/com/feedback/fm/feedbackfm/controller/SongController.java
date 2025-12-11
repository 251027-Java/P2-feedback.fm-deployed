package com.feedback.fm.feedbackfm.controller;

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
			Map.of("songId", "song1", "name", "Come Together", "durationMs", 259000, "href", "https://open.spotify.com/track/2EqlS6tkEnglzr7tkKAAYD"),
			Map.of("songId", "song2", "name", "Get Lucky", "durationMs", 248000, "href", "https://open.spotify.com/track/2Foc5Q5nqNiosCNqttzHof")
		);
		return ResponseEntity.ok(songs);
	}

	// Get specific song by ID
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getSongById(@PathVariable String id) {
		Map<String, Object> song = Map.of(
			"songId", id,
			"name", "Come Together",
			"durationMs", 259000,
			"href", "https://open.spotify.com/track/2EqlS6tkEnglzr7tkKAAYD"
		);
		return ResponseEntity.ok(song);
	}

	// Create a new song (admin only)
	@PostMapping
	public ResponseEntity<Map<String, Object>> createSong(@RequestBody Map<String, Object> songData) {
		Map<String, Object> newSong = Map.of(
			"songId", "new-song-id",
			"name", songData.getOrDefault("name", "Untitled"),
			"durationMs", songData.getOrDefault("durationMs", 180000),
			"message", "Song created successfully"
		);
		return ResponseEntity.status(201).body(newSong);
	}

	// Update song details (admin only)
	@PutMapping("/{id}")
	public ResponseEntity<Map<String, Object>> updateSong(@PathVariable String id, @RequestBody Map<String, Object> updates) {
		Map<String, Object> updatedSong = Map.of(
			"songId", id,
			"name", updates.getOrDefault("name", "Updated Song"),
			"message", "Song updated successfully"
		);
		return ResponseEntity.ok(updatedSong);
	}

	// Delete a song (admin only)
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteSong(@PathVariable String id) {
		Map<String, String> response = Map.of("message", "Song " + id + " deleted successfully");
		return ResponseEntity.ok(response);
	}

	// Get songs by artist
	@GetMapping("/by-artist/{artistId}")
	public ResponseEntity<List<Map<String, Object>>> getSongsByArtist(@PathVariable String artistId) {
		List<Map<String, Object>> songs = List.of(
			Map.of("songId", "song1", "name", "Come Together", "durationMs", 259000),
			Map.of("songId", "song3", "name", "Something", "durationMs", 182000)
		);
		return ResponseEntity.ok(songs);
	}

	// Get songs by album
	@GetMapping("/by-album/{albumId}")
	public ResponseEntity<List<Map<String, Object>>> getSongsByAlbum(@PathVariable String albumId) {
		List<Map<String, Object>> songs = List.of(
			Map.of("songId", "song1", "name", "Come Together", "durationMs", 259000),
			Map.of("songId", "song2", "name", "Something", "durationMs", 182000)
		);
		return ResponseEntity.ok(songs);
	}

	// Search for songs
	@GetMapping("/search")
	public ResponseEntity<List<Map<String, Object>>> searchSongs(@RequestParam String query) {
		List<Map<String, Object>> results = List.of(
			Map.of("songId", "song1", "name", "Come Together"),
			Map.of("songId", "song5", "name", "Come As You Are")
		);
		return ResponseEntity.ok(results);
	}

	// Like a song
	@PostMapping("/{id}/like")
	public ResponseEntity<Map<String, String>> likeSong(@PathVariable String id, @RequestParam String userId) {
		Map<String, String> response = Map.of("message", "Song " + id + " liked by user " + userId);
		return ResponseEntity.ok(response);
	}

	// Unlike a song
	@DeleteMapping("/{id}/like")
	public ResponseEntity<Map<String, String>> unlikeSong(@PathVariable String id, @RequestParam String userId) {
		Map<String, String> response = Map.of("message", "Song " + id + " unliked by user " + userId);
		return ResponseEntity.ok(response);
	}
}
