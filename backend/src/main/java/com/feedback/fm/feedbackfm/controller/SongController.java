package com.feedback.fm.feedbackfm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.feedback.fm.feedbackfm.dtos.SongDTO;
import com.feedback.fm.feedbackfm.service.SongService;
import com.feedback.fm.feedbackfm.service.spotify.SpotifyApiService;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "http://localhost:3000")
public class SongController {

	private final SongService songService;
	private final SpotifyApiService spotifyApiService;

	public SongController(SongService songService, SpotifyApiService spotifyApiService) {
		this.songService = songService;
		this.spotifyApiService = spotifyApiService;
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

	// Get top songs (Spotify integration)
	@GetMapping("/top")
	public ResponseEntity<List<Map<String, Object>>> getTopSongs(
			@RequestParam(required = false, defaultValue = "medium_term") String time_range,
			@RequestHeader(value = "X-Spotify-Token", required = false) String spotifyToken) {
		
		if (spotifyToken == null || spotifyToken.isBlank()) {
			return ResponseEntity.status(401).body(List.of());
		}
		
		try {
			Map<String, Object> spotifyResponse = spotifyApiService.getTopTracks(spotifyToken, time_range);
			
			if (spotifyResponse == null || spotifyResponse.isEmpty()) {
				return ResponseEntity.ok(List.of());
			}
			
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> items = (List<Map<String, Object>>) spotifyResponse.get("items");
			if (items == null || items.isEmpty()) {
				return ResponseEntity.ok(List.of());
			}
			
			// Convert Spotify response to frontend format
			List<Map<String, Object>> songs = new ArrayList<>();
			for (Map<String, Object> item : items) {
				Map<String, Object> song = new HashMap<>();
				song.put("id", item.get("id"));
				song.put("name", item.get("name"));
				
				// Get artists
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> artists = (List<Map<String, Object>>) item.get("artists");
				String artistName = "Unknown Artist";
				if (artists != null && !artists.isEmpty()) {
					artistName = (String) artists.get(0).get("name");
				}
				song.put("artist", artistName);
				song.put("artistName", artistName);
				
				// Get album
				Map<String, Object> album = (Map<String, Object>) item.get("album");
				if (album != null) {
					song.put("album", album.get("name"));
				}
				
				// Get duration
				song.put("duration", item.get("duration_ms"));
				
				// Get external URLs
				@SuppressWarnings("unchecked")
				Map<String, Object> externalUrls = (Map<String, Object>) item.get("external_urls");
				if (externalUrls != null) {
					song.put("href", externalUrls.get("spotify"));
				}
				
				// Get album image
				if (album != null) {
					@SuppressWarnings("unchecked")
					List<Map<String, Object>> images = (List<Map<String, Object>>) album.get("images");
					if (images != null && !images.isEmpty()) {
						song.put("image", images.get(0).get("url"));
					}
				}
				
				songs.add(song);
			}
			
			return ResponseEntity.ok(songs);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(List.of());
		}
	}

	// Get currently playing song (Spotify integration)
	@GetMapping("/currently-playing")
	public ResponseEntity<Map<String, Object>> getCurrentlyPlaying(@RequestHeader(value = "X-Spotify-Token", required = false) String spotifyToken) {
		if (spotifyToken == null || spotifyToken.isBlank()) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("isPlaying", false);
			errorResponse.put("error", "Spotify access token required. Please login again.");
			return ResponseEntity.status(401).body(errorResponse);
		}
		
		try {
			Map<String, Object> spotifyResponse = spotifyApiService.getCurrentlyPlaying(spotifyToken);
			
			if (spotifyResponse == null || spotifyResponse.isEmpty()) {
				// No track currently playing
				Map<String, Object> response = new HashMap<>();
				response.put("isPlaying", false);
				response.put("name", "");
				response.put("artist", "");
				response.put("album", "");
				return ResponseEntity.ok(response);
			}
			
			// Extract track information from Spotify response
			Map<String, Object> item = (Map<String, Object>) spotifyResponse.get("item");
			if (item == null) {
				Map<String, Object> response = new HashMap<>();
				response.put("isPlaying", false);
				response.put("name", "");
				response.put("artist", "");
				response.put("album", "");
				return ResponseEntity.ok(response);
			}
			
			// Get track name
			String trackName = (String) item.get("name");
			
			// Get artists
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> artists = (List<Map<String, Object>>) item.get("artists");
			String artistName = "Unknown Artist";
			if (artists != null && !artists.isEmpty()) {
				artistName = (String) artists.get(0).get("name");
			}
			
			// Get album
			Map<String, Object> album = (Map<String, Object>) item.get("album");
			String albumName = "Unknown Album";
			String albumImage = null;
			if (album != null) {
				albumName = (String) album.get("name");
				// Get album image
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> images = (List<Map<String, Object>>) album.get("images");
				if (images != null && !images.isEmpty()) {
					albumImage = (String) images.get(0).get("url");
				}
			}
			
			// Get playing status
			Boolean isPlaying = (Boolean) spotifyResponse.get("is_playing");
			
			Map<String, Object> response = new HashMap<>();
			response.put("isPlaying", isPlaying != null && isPlaying);
			response.put("name", trackName != null ? trackName : "");
			response.put("artist", artistName);
			response.put("album", albumName);
			response.put("image", albumImage);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("isPlaying", false);
			errorResponse.put("error", "Failed to get currently playing track: " + e.getMessage());
			return ResponseEntity.status(500).body(errorResponse);
		}
	}
}
