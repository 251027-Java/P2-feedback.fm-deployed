package com.feedback.fm.feedbackfm.controller;

import com.feedback.fm.feedbackfm.dtos.AlbumDTO;
import com.feedback.fm.feedbackfm.service.AlbumService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@CrossOrigin(origins = "http://localhost:3000")
public class AlbumController {

	private final AlbumService albumService;

	public AlbumController(AlbumService albumService) {
		this.albumService = albumService;
	}


	// Get all albums for a user or by artist
	@GetMapping
	public ResponseEntity<List<AlbumDTO>> getAllAlbums(@RequestParam(required = false) String userId, @RequestParam(required = false) String artistId) {
		if (artistId != null && !artistId.isBlank()) {
			return ResponseEntity.ok(albumService.findByArtistId(artistId));
		}
		return ResponseEntity.ok(albumService.getAllAlbums());
	}


	// Get specific album by ID
	@GetMapping("/{id}")
	public ResponseEntity<AlbumDTO> getAlbumById(@PathVariable String id) {
		return albumService.getById(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}


	// Create a new album ADMIN ONLY (we can take this out later if not needed)
	@PostMapping
	public ResponseEntity<AlbumDTO> createAlbum(@RequestBody AlbumDTO albumDTO) {
		AlbumDTO created = albumService.create(albumDTO);
		return ResponseEntity.status(201).body(created);
	}


	// Update album details ADMIN ONLY (we can take this out later if not needed)
	@PutMapping("/{id}")
	public ResponseEntity<AlbumDTO> updateAlbum(@PathVariable String id, @RequestBody AlbumDTO albumDTO) {
		AlbumDTO updated = albumService.update(id, albumDTO);
		return ResponseEntity.ok(updated);
	}


	// Delete an album ADMIN ONLY (we can take this out later if not needed)
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAlbum(@PathVariable String id) {
		albumService.delete(id);
		return ResponseEntity.noContent().build();
	}


	// Get songs for an album
	@GetMapping("/{id}/songs")
	public ResponseEntity<AlbumDTO> getAlbumSongs(@PathVariable String id) {
		return albumService.getById(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}


	// Search albums by name
	@GetMapping("/search")
	public ResponseEntity<List<AlbumDTO>> searchAlbums(@RequestParam String query) {
		return ResponseEntity.ok(albumService.searchByTitle(query));
	}

}
