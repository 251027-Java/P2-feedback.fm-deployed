package com.feedback.fm.Feedback_fm.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "http://localhost:3000")
public class SongController {

	// Get all songs
	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> getAllSongs( @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String query) {
		return null;
	}

	// Get a specific song by ID
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getSongById(@PathVariable Long id) {
		return null;
	}

	// Create a new song ADMIN ONLY (we can take this out later if not needed)
	@PostMapping
	public ResponseEntity<Map<String, Object>> createSong(@RequestBody Map<String, Object> songData) {
		return null;
	}

	// Update a song ADMIN ONLY (we can take this out later if not needed)
	@PutMapping("/{id}")
	public ResponseEntity<Map<String, Object>> updateSong(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
		return null;
	}

	// Delete a song ADMIN ONLY (we can take this out later if not needed)
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteSong(@PathVariable Long id) {
		return null;
	}

	// Get songs by artist
	@GetMapping("/by-artist/{artistId}")
	public ResponseEntity<List<Map<String, Object>>> getSongsByArtist(@PathVariable Long artistId) {
		return null;
	}

	// Get songs by album
	@GetMapping("/by-album/{albumId}")
	public ResponseEntity<List<Map<String, Object>>> getSongsByAlbum(@PathVariable Long albumId) {
		return null;
	}

	// Search songs by title
	@GetMapping("/search")
	public ResponseEntity<List<Map<String, Object>>> searchSongs(@RequestParam String query) {
		return null;
	}

	// Like a song (add to users library)
	@PostMapping("/{id}/like")
	public ResponseEntity<Map<String, String>> likeSong(@PathVariable Long id, @RequestParam Long userId) {
		return null;
	}

	// Unlike a song (remove from user library)
	@DeleteMapping("/{id}/like")
	public ResponseEntity<Map<String, String>> unlikeSong(@PathVariable Long id, @RequestParam Long userId) {
		return null;
	}
}
