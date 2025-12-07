package com.feedback.fm.Feedback_fm.Controller;

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
		return null;
	}

	// Get a specific artist by ID
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getArtistById(@PathVariable Long id) {
		return null;
	}

	// Create a new artist ADMIN ONLY (we can take this out later if not needed)
	@PostMapping
	public ResponseEntity<Map<String, Object>> createArtist(@RequestBody Map<String, Object> artistData) {
		return null;
	}

	// Update artist details ADMIN ONLY (we can take this out later if not needed)
	@PutMapping("/{id}")
	public ResponseEntity<Map<String, Object>> updateArtist(
			@PathVariable Long id,
			@RequestBody Map<String, Object> updates) {
		return null;
	}

	// Delete an artist ADMIN ONLY (we can take this out later if not needed)
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteArtist(@PathVariable Long id) {
		return null;
	}

	// Get an artist's top songs
	@GetMapping("/{id}/songs")
	public ResponseEntity<List<Map<String, Object>>> getArtistSongs(@PathVariable Long id) {
		return null;
	}

	// Get an artist's albums
	@GetMapping("/{id}/albums")
	public ResponseEntity<List<Map<String, Object>>> getArtistAlbums(@PathVariable Long id) {
		return null;
	}

}
