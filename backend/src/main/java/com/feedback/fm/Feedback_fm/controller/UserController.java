package com.feedback.fm.Feedback_fm.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    // Get user profile by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable Long id) {
        Map<String, Object> user = Map.of(
            "userId", id,
            "username", "john_doe",
            "email", "john@example.com",
            "spotifyUserId", "spotify123"
        );
        return ResponseEntity.ok(user);
    }

    // Get complete dashboard data for a user
    @GetMapping("/{id}/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@PathVariable Long id) {
        Map<String, Object> dashboard = Map.of(
            "userId", id,
            "username", "john_doe",
            "stats", Map.of(
                "totalListeningTime", "187 hours",
                "songsPlayed", 3492,
                "currentStreak", 15
            ),
            "topArtists", List.of(
                Map.of("artistId", 1L, "name", "The Beatles", "plays", 150),
                Map.of("artistId", 2L, "name", "Daft Punk", "plays", 142)
            ),
            "topSongs", List.of(
                Map.of("songId", 1L, "title", "Come Together", "plays", 45),
                Map.of("songId", 2L, "title", "Get Lucky", "plays", 42)
            )
        );
        return ResponseEntity.ok(dashboard);
    }

    // Get user statistics summary
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable Long id) {
        Map<String, Object> stats = Map.of(
            "totalListeningTime", "187 hours",
            "songsPlayed", 3492,
            "songsPlayedToday", 42,
            "currentStreak", 15,
            "topGenre", "Pop"
        );
        return ResponseEntity.ok(stats);
    }

    // Update user profile
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Map<String, Object> updatedUser = Map.of(
            "userId", id,
            "username", updates.getOrDefault("username", "john_doe"),
            "email", updates.getOrDefault("email", "john@example.com"),
            "message", "User updated successfully"
        );
        return ResponseEntity.ok(updatedUser);
    }

    // Delete user account
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        Map<String, String> response = Map.of("message", "User " + id + " deleted successfully");
        return ResponseEntity.ok(response);
    }

    // Simple login endpoint
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        if ("demo".equals(username) && "password".equals(password)) {
            Map<String, Object> response = Map.of(
                "userId", 1L,
                "username", username,
                "token", "mock-jwt-token-12345",
                "message", "Login successful"
            );
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> userInfo) {
        Map<String, Object> response = Map.of(
            "userId", 999L,
            "username", userInfo.get("username"),
            "email", userInfo.get("email"),
            "message", "User registered successfully"
        );
        return ResponseEntity.status(201).body(response);
    }
}
