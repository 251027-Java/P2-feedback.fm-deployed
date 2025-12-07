package com.feedback.fm.Feedback_fm.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "http://localhost:3000")
public class HistoryController {

	// Get listening history for a user with optional date range
	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> getListeningHistory(@RequestParam Long userId, @RequestParam(required = false) String dateRange, @RequestParam(required = false) String query) {
		return null;
	}

	// Get recently played tracks for a user
	@GetMapping("/recent")
	public ResponseEntity<List<Map<String, Object>>> getRecentlyPlayed(@RequestParam Long userId, @RequestParam(defaultValue = "20") int limit) {
		return null;
	}

	// Record a play event (user listened to a song)
	@PostMapping
	public ResponseEntity<Map<String, Object>> addHistoryRecord(@RequestBody Map<String, Object> historyData) {
		return null;
	}

	// Delete a specific history record
	@DeleteMapping("/{historyId}")
	public ResponseEntity<Map<String, String>> deleteHistoryRecord(@PathVariable Long historyId) {
		return null;
	}

	// Get history stats for a user
	@GetMapping("/stats")
	public ResponseEntity<Map<String, Object>> getHistoryStats(@RequestParam Long userId) {
		return null;
	}
}
