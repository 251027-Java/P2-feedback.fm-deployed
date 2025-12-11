package com.feedback.fm.feedbackfm.controller;

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
			Map.of("artistId", "artist1", "name", "The Beatles", "href", "https://open.spotify.com/artist/3WrFJ7ztbogyGnTHbHJFl2"),
			Map.of("artistId", "artist2", "name", "Daft Punk", "href", "https://open.spotify.com/artist/4tZwfgrHOc3mvqYlEYSvVi")
		);
		return ResponseEntity.ok(artists);
	}

	// Get specific artist by ID
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getArtistById(@PathVariable String id) {
		Map<String, Object> artist = Map.of(
			"artistId", id,
			"name", "The Beatles",
			"href", "https://open.spotify.com/artist/3WrFJ7ztbogyGnTHbHJFl2"
		);
		return ResponseEntity.ok(artist);
	}

	// Create a new artist (admin only)
	@PostMapping
	public ResponseEntity<Map<String, Object>> createArtist(@RequestBody Map<String, Object> artistData) {
		Map<String, Object> newArtist = Map.of(
			"artistId", "new-artist-id",
			"name", artistData.getOrDefault("name", "Unknown Artist"),
			"href", artistData.getOrDefault("href", ""),
			"message", "Artist created successfully"
		);
		return ResponseEntity.status(201).body(newArtist);
	}

	// Update artist details (admin only)
	@PutMapping("/{id}")
	public ResponseEntity<Map<String, Object>> updateArtist(
			@PathVariable String id,
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
	public ResponseEntity<Map<String, String>> deleteArtist(@PathVariable String id) {
		Map<String, String> response = Map.of("message", "Artist " + id + " deleted successfully");
		return ResponseEntity.ok(response);
	}

	// Get songs by artist
	@GetMapping("/{id}/songs")
	public ResponseEntity<List<Map<String, Object>>> getArtistSongs(@PathVariable String id) {
		List<Map<String, Object>> songs = List.of(
			Map.of("songId", "song1", "name", "Come Together", "durationMs", 259000),
			Map.of("songId", "song3", "name", "Something", "durationMs", 182000)
		);
		return ResponseEntity.ok(songs);
	}

	// Get albums by artist
	@GetMapping("/{id}/albums")
	public ResponseEntity<List<Map<String, Object>>> getArtistAlbums(@PathVariable String id) {
		List<Map<String, Object>> albums = List.of(
			Map.of("albumId", "album1", "title", "Abbey Road", "releaseYear", 1969),
			Map.of("albumId", "album2", "title", "Let It Be", "releaseYear", 1970)
		);
		return ResponseEntity.ok(albums);
	}

}
