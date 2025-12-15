package com.feedback.fm.feedbackfm.controller;

import java.time.Instant;
import java.time.ZoneId;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.feedback.fm.feedbackfm.dtos.HistoryDTO;
import com.feedback.fm.feedbackfm.service.HistoryService;
import com.feedback.fm.feedbackfm.service.spotify.SpotifyApiService;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "http://localhost:3000")
public class HistoryController {

	private final HistoryService historyService;
	private final SpotifyApiService spotifyApiService;

	public HistoryController(HistoryService historyService, SpotifyApiService spotifyApiService) {
		this.historyService = historyService;
		this.spotifyApiService = spotifyApiService;
	}

	// Get listening history for a user with optional filters (uses Spotify API if token provided)
	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> getListeningHistory(
			@RequestParam(required = false) String listenerId, 
			@RequestParam(required = false) Integer limit,
			@RequestParam(required = false) String dateRange, 
			@RequestParam(required = false) String query,
			@RequestHeader(value = "X-Spotify-Token", required = false) String spotifyToken) {
		
		int historyLimit = (limit != null && limit > 0) ? limit : 50;
		
		// Use Spotify API if token is provided
		if (spotifyToken != null && !spotifyToken.isBlank()) {
			try {
				Map<String, Object> spotifyResponse = spotifyApiService.getRecentlyPlayed(spotifyToken, historyLimit);
				
				if (spotifyResponse == null) {
					System.out.println("Spotify API returned null response");
					return ResponseEntity.ok(List.of());
				}
				
				System.out.println("Spotify response keys: " + spotifyResponse.keySet());
				
				if (spotifyResponse.containsKey("items")) {
					@SuppressWarnings("unchecked")
					List<Map<String, Object>> items = (List<Map<String, Object>>) spotifyResponse.get("items");
					
					if (items == null) {
						System.out.println("Items list is null");
						return ResponseEntity.ok(List.of());
					}
					
					System.out.println("Found " + items.size() + " items in recently played");
					
					if (items.isEmpty()) {
						System.out.println("Items list is empty - no recently played tracks");
						return ResponseEntity.ok(List.of());
					}
					
					List<Map<String, Object>> history = new ArrayList<>();
					for (Map<String, Object> item : items) {
						Map<String, Object> track = (Map<String, Object>) item.get("track");
						
						if (track != null) {
							Map<String, Object> historyItem = new HashMap<>();
							historyItem.put("id", track.get("id"));
							historyItem.put("songName", track.get("name"));
							
							// Get artist name
							@SuppressWarnings("unchecked")
							List<Map<String, Object>> artists = (List<Map<String, Object>>) track.get("artists");
							String artistName = "Unknown Artist";
							if (artists != null && !artists.isEmpty()) {
								artistName = (String) artists.get(0).get("name");
							}
							historyItem.put("artistName", artistName);
							
							// Get album image (matching SongController pattern)
							@SuppressWarnings("unchecked")
							Map<String, Object> album = (Map<String, Object>) track.get("album");
							if (album != null) {
								@SuppressWarnings("unchecked")
								List<Map<String, Object>> images = (List<Map<String, Object>>) album.get("images");
								if (images != null && !images.isEmpty()) {
									historyItem.put("image", images.get(0).get("url"));
								}
							}
							
							// Get Spotify URL
							@SuppressWarnings("unchecked")
							Map<String, Object> externalUrls = (Map<String, Object>) track.get("external_urls");
							if (externalUrls != null) {
								historyItem.put("href", externalUrls.get("spotify"));
							}
							
							// Get played at timestamp
							Object playedAtObj = item.get("played_at");
							if (playedAtObj != null) {
								if (playedAtObj instanceof String) {
									String playedAtStr = (String) playedAtObj;
									try {
										Instant instant = Instant.parse(playedAtStr);
										historyItem.put("playedAt", instant.atZone(ZoneId.systemDefault()).toLocalDateTime().toString());
									} catch (Exception e) {
										historyItem.put("playedAt", playedAtStr);
									}
								} else {
									historyItem.put("playedAt", playedAtObj.toString());
								}
							}
							
							history.add(historyItem);
						}
					}
					
					System.out.println("Returning " + history.size() + " history items");
					return ResponseEntity.ok(history);
				} else {
					System.out.println("Spotify response does not contain 'items' key");
					return ResponseEntity.ok(List.of());
				}
			} catch (Exception e) {
				// Log error and return empty list instead of falling back
				System.err.println("Error getting recently played from Spotify: " + e.getMessage());
				e.printStackTrace();
				return ResponseEntity.ok(List.of());
			}
		} else {
			System.out.println("No Spotify token provided for history request");
		}
		
		// Fallback to database
		List<HistoryDTO> dbHistory;
		if (listenerId != null && !listenerId.isBlank()) {
			dbHistory = historyService.findByListenerId(listenerId);
		} else {
			dbHistory = historyService.getAllHistory();
		}
		
		if (historyLimit > 0) {
			dbHistory = dbHistory.stream().limit(historyLimit).toList();
		}
		
		// Convert to map format
		List<Map<String, Object>> historyMaps = dbHistory.stream()
			.map(dto -> {
				Map<String, Object> map = new HashMap<>();
				map.put("id", dto.historyId());
				map.put("songName", dto.songId()); // Song ID as placeholder
				map.put("artistName", "Unknown Artist");
				if (dto.playedAt() != null) {
					map.put("playedAt", dto.playedAt().toString());
				}
				return map;
			})
			.toList();
		
		return ResponseEntity.ok(historyMaps);
	}

	// Get recently played songs
	@GetMapping("/recent")
	public ResponseEntity<List<HistoryDTO>> getRecentlyPlayed(@RequestParam String listenerId, @RequestParam(defaultValue = "20") int limit) {
		return ResponseEntity.ok(historyService.getRecentHistoryByListener(listenerId, limit));
	}

	// Add a listening history record
	@PostMapping
	public ResponseEntity<HistoryDTO> addHistoryRecord(@RequestBody HistoryDTO historyDTO) {
		HistoryDTO created = historyService.create(historyDTO);
		return ResponseEntity.status(201).body(created);
	}

	// Delete a listening history record
	@DeleteMapping("/{historyId}")
	public ResponseEntity<Void> deleteHistoryRecord(@PathVariable Long historyId) {
		historyService.delete(historyId);
		return ResponseEntity.noContent().build();
	}

	// Get listening statistics for a user
	@GetMapping("/stats")
	public ResponseEntity<Map<String, Object>> getHistoryStats(@RequestParam String listenerId) {
		List<HistoryDTO> history = historyService.findByListenerId(listenerId);
		Map<String, Object> stats = Map.of(
			"totalSongsPlayed", history.size(),
			"totalListeningTime", "0 hours",
			"topGenre", "N/A",
			"currentStreak", 0
		);
		return ResponseEntity.ok(stats);
	}
}
