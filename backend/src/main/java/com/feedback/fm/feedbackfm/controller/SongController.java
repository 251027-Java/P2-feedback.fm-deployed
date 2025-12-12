package com.feedback.fm.feedbackfm.controller;

import com.feedback.fm.feedbackfm.dtos.SongDTO;
import com.feedback.fm.feedbackfm.service.SongService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "http://localhost:3000")
public class SongController {

	private final SongService songService;

	public SongController(SongService songService) {
		this.songService = songService;
	}

	// Get all songs with pagination
	@GetMapping
	public ResponseEntity<List<SongDTO>> getAllSongs(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String query) {
		if (query != null && !query.isBlank()) {
			return ResponseEntity.ok(songService.searchByName(query));
		}
		return ResponseEntity.ok(songService.getAllSongs());
	}

	// Get specific song by ID
	@GetMapping("/{id}")
	public ResponseEntity<SongDTO> getSongById(@PathVariable String id) {
		return songService.getById(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	// Create a new song (admin only)
	@PostMapping
	public ResponseEntity<SongDTO> createSong(@RequestBody SongDTO songDTO) {
		SongDTO created = songService.create(songDTO);
		return ResponseEntity.status(201).body(created);
	}

	// Update song details (admin only)
	@PutMapping("/{id}")
	public ResponseEntity<SongDTO> updateSong(@PathVariable String id, @RequestBody SongDTO songDTO) {
		SongDTO updated = songService.update(id, songDTO);
		return ResponseEntity.ok(updated);
	}

	// Delete a song (admin only)
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSong(@PathVariable String id) {
		songService.delete(id);
		return ResponseEntity.noContent().build();
	}

	// Get songs by artist
	@GetMapping("/by-artist/{artistId}")
	public ResponseEntity<List<SongDTO>> getSongsByArtist(@PathVariable String artistId) {
		return ResponseEntity.ok(List.of());
	}

	// Get songs by album
	@GetMapping("/by-album/{albumId}")
	public ResponseEntity<List<SongDTO>> getSongsByAlbum(@PathVariable String albumId) {
		return ResponseEntity.ok(List.of());
	}

	// Search for songs
	@GetMapping("/search")
	public ResponseEntity<List<SongDTO>> searchSongs(@RequestParam String query) {
		return ResponseEntity.ok(songService.searchByName(query));
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
