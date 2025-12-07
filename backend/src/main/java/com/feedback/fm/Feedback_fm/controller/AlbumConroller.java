package com.feedback.fm.Feedback_fm.Controller;

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
	public ResponseEntity<List<Map<String, Object>>> getAllAlbums( @RequestParam(required = false) Long userId,@RequestParam(required = false) Long artistId) {
		return null;
	}

	// Get specific album by ID
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getAlbumById(@PathVariable Long id) {
		return null;
	}

	// Create a new album ADMIN ONLY (we can take this out later if not needed)
	@PostMapping
	public ResponseEntity<Map<String, Object>> createAlbum(@RequestBody Map<String, Object> albumData) {
		return null;
	}

	// Update album details ADMIN ONLY (we can take this out later if not needed)
	@PutMapping("/{id}")
	public ResponseEntity<Map<String, Object>> updateAlbum(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
		return null;
	}

	// Delete an album ADMIN ONLY (we can take this out later if not needed)
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteAlbum(@PathVariable Long id) {
		return null;
	}

	// Get songs for an album
	@GetMapping("/{id}/songs")
	public ResponseEntity<List<Map<String, Object>>> getAlbumSongs(@PathVariable Long id) {
		return null;
	}

	// Search albums by name
	@GetMapping("/search")
	public ResponseEntity<List<Map<String, Object>>> searchAlbums(@RequestParam String query) {
		return null;
	}

}
