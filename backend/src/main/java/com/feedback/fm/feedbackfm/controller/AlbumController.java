package com.feedback.fm.feedbackfm.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/albums")
@CrossOrigin(origins = "http://localhost:3000")
public class AlbumController {


	// Get all albums for a user or by artist
	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> getAllAlbums(@RequestParam(required = false) String userId, @RequestParam(required = false) String artistId) {

		List<Map<String, Object>> albums = List.of(
			Map.of("albumId", "album1", "title", "Abbey Road", "artistId", "artist1", "releaseYear", 1969),
			Map.of("albumId", "album2", "title", "Random Access Memories", "artistId", "artist2", "releaseYear", 2013)
		);
		return ResponseEntity.ok(albums);
	}


	// Get specific album by ID
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getAlbumById(@PathVariable String id) {

		Map<String, Object> album = Map.of(
			"albumId", id,
			"title", "Abbey Road",
			"artistId", "artist1",
			"releaseYear", 1969,
			"href", "https://open.spotify.com/album/0ETFjACtuP2ADo6LFhL6HN"
		);
		return ResponseEntity.ok(album);
	}


	// Create a new album ADMIN ONLY (we can take this out later if not needed)
	@PostMapping
	public ResponseEntity<Map<String, Object>> createAlbum(@RequestBody Map<String, Object> albumData) {

		Map<String, Object> newAlbum = Map.of(
			"albumId", "new-album-id",
			"title", albumData.getOrDefault("title", "Untitled Album"),
			"artistId", albumData.getOrDefault("artistId", "unknown-artist"),
			"releaseYear", albumData.getOrDefault("releaseYear", 2025),
			"message", "Album created successfully"
		);
		return ResponseEntity.status(201).body(newAlbum);
	}


	// Update album details ADMIN ONLY (we can take this out later if not needed)
	@PutMapping("/{id}")
	public ResponseEntity<Map<String, Object>> updateAlbum(@PathVariable String id, @RequestBody Map<String, Object> updates) {

		Map<String, Object> updatedAlbum = Map.of(
			"albumId", id,
			"title", updates.getOrDefault("title", "Updated Album"),
			"artistId", updates.getOrDefault("artistId", "updated-artist"),
			"releaseYear", updates.getOrDefault("releaseYear", 2025),
			"message", "Album updated successfully"
		);
		return ResponseEntity.ok(updatedAlbum);
	}


	// Delete an album ADMIN ONLY (we can take this out later if not needed)
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteAlbum(@PathVariable String id) {

		Map<String, String> response = Map.of(
			"message", "Album " + id + " deleted successfully"
		);
		return ResponseEntity.ok(response);
	}


	// Get songs for an album
	@GetMapping("/{id}/songs")
	public ResponseEntity<List<Map<String, Object>>> getAlbumSongs(@PathVariable String id) {

		List<Map<String, Object>> songs = List.of(
			Map.of("songId", "song1", "name", "Come Together", "durationMs", 259000),
			Map.of("songId", "song2", "name", "Something", "durationMs", 182000)
		);
		return ResponseEntity.ok(songs);
	}


	// Search albums by name
	@GetMapping("/search")
	public ResponseEntity<List<Map<String, Object>>> searchAlbums(@RequestParam String query) {

		List<Map<String, Object>> results = List.of(
			Map.of("albumId", "album1", "title", "Abbey Road", "artistId", "artist1"),
			Map.of("albumId", "album3", "title", "Abacab", "artistId", "artist3")
		);
		return ResponseEntity.ok(results);
	}

}
