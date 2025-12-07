package com.feedback.fm.Feedback_fm.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/artists")
@CrossOrigin(origins = "http://localhost:3000")
public class ArtistController {

	// Get all artists
	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> getAllArtists(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String query) {
		List<Map<String, Object>> artists = List.of(
			Map.of("artistId", 1L, "name", "The Beatles", "genre", "Rock", "popularity", 95),
			Map.of("artistId", 2L, "name", "Daft Punk", "genre", "Electronic", "popularity", 88)
		);
		return ResponseEntity.ok(artists);
	}

	// Get specific artist by ID
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getArtistById(@PathVariable Long id) {
		Map<String, Object> artist = Map.of(
			"artistId", id,
			"name", "The Beatles",
			"genre", "Rock",
			"popularity", 95,
			"followers", 2000000
		);
		return ResponseEntity.ok(artist);
	}

	// Create a new artist (admin only)
	@PostMapping
	public ResponseEntity<Map<String, Object>> createArtist(@RequestBody Map<String, Object> artistData) {
		Map<String, Object> newArtist = Map.of(
			"artistId", 999L,
			"name", artistData.getOrDefault("name", "Unknown Artist"),
			"genre", artistData.getOrDefault("genre", "Unknown"),
			"message", "Artist created successfully"
		);
		return ResponseEntity.status(201).body(newArtist);
	}

	// Update artist details (admin only)
	@PutMapping("/{id}")
	public ResponseEntity<Map<String, Object>> updateArtist(
			@PathVariable Long id,
			@RequestBody Map<String, Object> updates) {
		Map<String, Object> updatedArtist = Map.of(
			"artistId", id,
			"name", updates.getOrDefault("name", "Updated Artist"),
			"message", "Artist updated successfully"
		);
		return ResponseEntity.ok(updatedArtist);
	}

	// Delete an artist (admin only)
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteArtist(@PathVariable Long id) {
		Map<String, String> response = Map.of("message", "Artist " + id + " deleted successfully");
		return ResponseEntity.ok(response);
	}

	// Get songs by artist
	@GetMapping("/{id}/songs")
	public ResponseEntity<List<Map<String, Object>>> getArtistSongs(@PathVariable Long id) {
		List<Map<String, Object>> songs = List.of(
			Map.of("songId", 1L, "title", "Come Together", "duration", 259),
			Map.of("songId", 3L, "title", "Something", "duration", 182)
		);
		return ResponseEntity.ok(songs);
	}

	// Get albums by artist
	@GetMapping("/{id}/albums")
	public ResponseEntity<List<Map<String, Object>>> getArtistAlbums(@PathVariable Long id) {
		List<Map<String, Object>> albums = List.of(
			Map.of("albumId", 1L, "title", "Abbey Road", "year", 1969),
			Map.of("albumId", 2L, "title", "Let It Be", "year", 1970)
		);
		return ResponseEntity.ok(albums);
	}

}
