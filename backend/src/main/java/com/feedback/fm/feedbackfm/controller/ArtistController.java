package com.feedback.fm.feedbackfm.controller;

import com.feedback.fm.feedbackfm.dtos.ArtistDTO;
import com.feedback.fm.feedbackfm.service.ArtistService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@CrossOrigin(origins = "http://localhost:3000")
public class ArtistController {

	private final ArtistService artistService;

	public ArtistController(ArtistService artistService) {
		this.artistService = artistService;
	}

	// Get all artists
	@GetMapping
	public ResponseEntity<List<ArtistDTO>> getAllArtists(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String query) {
		if (query != null && !query.isBlank()) {
			return ResponseEntity.ok(artistService.searchByName(query));
		}
		return ResponseEntity.ok(artistService.getAllArtists());
	}

	// Get specific artist by ID
	@GetMapping("/{id}")
	public ResponseEntity<ArtistDTO> getArtistById(@PathVariable String id) {
		return artistService.getById(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	// Create a new artist (admin only)
	@PostMapping
	public ResponseEntity<ArtistDTO> createArtist(@RequestBody ArtistDTO artistDTO) {
		ArtistDTO created = artistService.create(artistDTO);
		return ResponseEntity.status(201).body(created);
	}

	// Update artist details (admin only)
	@PutMapping("/{id}")
	public ResponseEntity<ArtistDTO> updateArtist(@PathVariable String id, @RequestBody ArtistDTO artistDTO) {
		ArtistDTO updated = artistService.update(id, artistDTO);
		return ResponseEntity.ok(updated);
	}

	// Delete an artist (admin only)
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteArtist(@PathVariable String id) {
		artistService.delete(id);
		return ResponseEntity.noContent().build();
	}

	// Get songs by artist (returns full artist DTO which includes songs)
	@GetMapping("/{id}/songs")
	public ResponseEntity<ArtistDTO> getArtistSongs(@PathVariable String id) {
		return artistService.getById(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	// Get albums by artist (returns full artist DTO which includes albums)
	@GetMapping("/{id}/albums")
	public ResponseEntity<ArtistDTO> getArtistAlbums(@PathVariable String id) {
		return artistService.getById(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

}
