package com.feedback.fm.feedbackfm.controller;

import com.feedback.fm.feedbackfm.dtos.ArtistDTO;
import com.feedback.fm.feedbackfm.service.ArtistService;
import com.feedback.fm.feedbackfm.service.spotify.SpotifyApiService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {

	private final ArtistService artistService;
	private final SpotifyApiService spotifyApiService;

	public ArtistController(ArtistService artistService, SpotifyApiService spotifyApiService) {
		this.artistService = artistService;
		this.spotifyApiService = spotifyApiService;
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

	// Get top artists (Spotify integration)
	@GetMapping("/top")
	public ResponseEntity<List<Map<String, Object>>> getTopArtists(
			@RequestParam(required = false, defaultValue = "medium_term") String time_range,
			@RequestHeader(value = "X-Spotify-Token", required = false) String spotifyToken) {
		
		if (spotifyToken == null || spotifyToken.isBlank()) {
			return ResponseEntity.status(401).body(List.of());
		}
		
		try {
			Map<String, Object> spotifyResponse = spotifyApiService.getTopArtists(spotifyToken, time_range);
			
			if (spotifyResponse == null || spotifyResponse.isEmpty()) {
				return ResponseEntity.ok(List.of());
			}
			
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> items = (List<Map<String, Object>>) spotifyResponse.get("items");
			if (items == null || items.isEmpty()) {
				return ResponseEntity.ok(List.of());
			}
			
			// Convert Spotify response to frontend format
			List<Map<String, Object>> artists = new ArrayList<>();
			for (Map<String, Object> item : items) {
				Map<String, Object> artist = new HashMap<>();
				artist.put("id", item.get("id"));
				artist.put("name", item.get("name"));
				
				// Get external URLs
				@SuppressWarnings("unchecked")
				Map<String, Object> externalUrls = (Map<String, Object>) item.get("external_urls");
				if (externalUrls != null) {
					artist.put("href", externalUrls.get("spotify"));
				}
				
				// Get images
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> images = (List<Map<String, Object>>) item.get("images");
				if (images != null && !images.isEmpty()) {
					artist.put("image", images.get(0).get("url"));
				}
				
				artists.add(artist);
			}
			
			return ResponseEntity.ok(artists);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(List.of());
		}
	}

}
