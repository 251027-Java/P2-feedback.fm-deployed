package com.feedback.fm.Feedback_fm.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "http://localhost:3000")
public class HistoryController {

	// Get listening history for a user with optional filters
	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> getListeningHistory(@RequestParam Long userId, @RequestParam(required = false) String dateRange, @RequestParam(required = false) String query) {
		List<Map<String, Object>> history = List.of(
			Map.of("historyId", 1L, "songTitle", "Come Together", "artist", "The Beatles", "playedAt", "2024-12-01T10:30:00"),
			Map.of("historyId", 2L, "songTitle", "Get Lucky", "artist", "Daft Punk", "playedAt", "2024-12-01T09:15:00")
		);
		return ResponseEntity.ok(history);
	}

	// Get recently played songs
	@GetMapping("/recent")
	public ResponseEntity<List<Map<String, Object>>> getRecentlyPlayed(@RequestParam Long userId, @RequestParam(defaultValue = "20") int limit) {
		List<Map<String, Object>> recentSongs = List.of(
			Map.of("songId", 1L, "title", "Come Together", "artist", "The Beatles", "playedAt", "2024-12-01T10:30:00"),
			Map.of("songId", 2L, "title", "Get Lucky", "artist", "Daft Punk", "playedAt", "2024-12-01T09:15:00")
		);
		return ResponseEntity.ok(recentSongs);
	}

	// Add a listening history record
	@PostMapping
	public ResponseEntity<Map<String, Object>> addHistoryRecord(@RequestBody Map<String, Object> historyData) {
		Map<String, Object> response = Map.of(
			"historyId", 999L,
			"userId", historyData.get("userId"),
			"songId", historyData.get("songId"),
			"playedAt", historyData.getOrDefault("playedAt", "2024-12-01T12:00:00"),
			"message", "History record added successfully"
		);
		return ResponseEntity.status(201).body(response);
	}

	// Delete a listening history record
	@DeleteMapping("/{historyId}")
	public ResponseEntity<Map<String, String>> deleteHistoryRecord(@PathVariable Long historyId) {
		Map<String, String> response = Map.of("message", "History record " + historyId + " deleted successfully");
		return ResponseEntity.ok(response);
	}

	// Get listening statistics for a user
	@GetMapping("/stats")
	public ResponseEntity<Map<String, Object>> getHistoryStats(@RequestParam Long userId) {
		Map<String, Object> stats = Map.of(
			"totalSongsPlayed", 3492,
			"totalListeningTime", "187 hours",
			"topGenre", "Pop",
			"currentStreak", 15
		);
		return ResponseEntity.ok(stats);
	}
}
