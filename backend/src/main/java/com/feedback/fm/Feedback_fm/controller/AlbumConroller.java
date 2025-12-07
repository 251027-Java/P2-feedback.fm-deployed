package com.feedback.fm.Feedback_fm.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/albums")
@CrossOrigin(origins = "http://localhost:3000")
public class AlbumConroller {


	// Get all albums for a user or by artist
	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> getAllAlbums(@RequestParam(required = false) Long userId, @RequestParam(required = false) Long artistId) {

		List<Map<String, Object>> albums = List.of(
			Map.of("albumId", 1L, "title", "Abbey Road", "artist", "The Beatles", "year", 1969),
			Map.of("albumId", 2L, "title", "Random Access Memories", "artist", "Daft Punk", "year", 2013)
		);
		return ResponseEntity.ok(albums);
	}


	// Get specific album by ID
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getAlbumById(@PathVariable Long id) {

		Map<String, Object> album = Map.of(
			"albumId", id,
			"title", "Abbey Road",
			"artist", "The Beatles",
			"year", 1969,
			"trackCount", 17
		);
		return ResponseEntity.ok(album);
	}


	// Create a new album ADMIN ONLY (we can take this out later if not needed)
	@PostMapping
	public ResponseEntity<Map<String, Object>> createAlbum(@RequestBody Map<String, Object> albumData) {

		Map<String, Object> newAlbum = Map.of(
			"albumId", 999L,
			"title", albumData.getOrDefault("title", "Untitled Album"),
			"artist", albumData.getOrDefault("artist", "Unknown Artist"),
			"year", albumData.getOrDefault("year", 2025),
			"message", "Album created successfully"
		);
		return ResponseEntity.status(201).body(newAlbum);
	}


	// Update album details ADMIN ONLY (we can take this out later if not needed)
	@PutMapping("/{id}")
	public ResponseEntity<Map<String, Object>> updateAlbum(@PathVariable Long id, @RequestBody Map<String, Object> updates) {

		Map<String, Object> updatedAlbum = Map.of(
			"albumId", id,
			"title", updates.getOrDefault("title", "Updated Album"),
			"artist", updates.getOrDefault("artist", "Updated Artist"),
			"year", updates.getOrDefault("year", 2025),
			"message", "Album updated successfully"
		);
		return ResponseEntity.ok(updatedAlbum);
	}


	// Delete an album ADMIN ONLY (we can take this out later if not needed)
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteAlbum(@PathVariable Long id) {

		Map<String, String> response = Map.of(
			"message", "Album " + id + " deleted successfully"
		);
		return ResponseEntity.ok(response);
	}


	// Get songs for an album
	@GetMapping("/{id}/songs")
	public ResponseEntity<List<Map<String, Object>>> getAlbumSongs(@PathVariable Long id) {

		List<Map<String, Object>> songs = List.of(
			Map.of("songId", 1L, "title", "Come Together", "duration", 259),
			Map.of("songId", 2L, "title", "Something", "duration", 182)
		);
		return ResponseEntity.ok(songs);
	}


	// Search albums by name
	@GetMapping("/search")
	public ResponseEntity<List<Map<String, Object>>> searchAlbums(@RequestParam String query) {

		List<Map<String, Object>> results = List.of(
			Map.of("albumId", 1L, "title", "Abbey Road", "artist", "The Beatles"),
			Map.of("albumId", 3L, "title", "Abacab", "artist", "Genesis")
		);
		return ResponseEntity.ok(results);
	}

}
