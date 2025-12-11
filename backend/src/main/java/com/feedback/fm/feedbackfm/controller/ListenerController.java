package com.feedback.fm.feedbackfm.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class ListenerController {

    // Get user profile by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable String id) {
        Map<String, Object> user = Map.of(
            "listenerId", id,
            "displayName", "John Doe",
            "email", "john@example.com",
            "country", "US",
            "href", "https://open.spotify.com/user/" + id
        );
        return ResponseEntity.ok(user);
    }

    // Get complete dashboard data for a user
    @GetMapping("/{id}/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@PathVariable String id) {
        Map<String, Object> dashboard = Map.of(
            "listenerId", id,
            "displayName", "John Doe",
            "stats", Map.of(
                "totalListeningTime", "187 hours",
                "songsPlayed", 3492,
                "currentStreak", 15
            ),
            "topArtists", List.of(
                Map.of("artistId", "artist1", "name", "The Beatles", "plays", 150),
                Map.of("artistId", "artist2", "name", "Daft Punk", "plays", 142)
            ),
            "topSongs", List.of(
                Map.of("songId", "song1", "name", "Come Together", "plays", 45),
                Map.of("songId", "song2", "name", "Get Lucky", "plays", 42)
            )
        );
        return ResponseEntity.ok(dashboard);
    }

    // Get user statistics summary
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String id) {
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
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        Map<String, Object> updatedUser = Map.of(
            "listenerId", id,
            "displayName", updates.getOrDefault("displayName", "John Doe"),
            "email", updates.getOrDefault("email", "john@example.com"),
            "country", updates.getOrDefault("country", "US"),
            "message", "User updated successfully"
        );
        return ResponseEntity.ok(updatedUser);
    }

    // Delete user account
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String id) {
        Map<String, String> response = Map.of("message", "User " + id + " deleted successfully");
        return ResponseEntity.ok(response);
    }

    // Simple login endpoint
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        if ("demo@example.com".equals(email) && "password".equals(password)) {
            Map<String, Object> response = Map.of(
                "listenerId", "demo-listener-id",
                "displayName", "Demo User",
                "email", email,
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
            "listenerId", "new-listener-id",
            "displayName", userInfo.get("displayName"),
            "email", userInfo.get("email"),
            "country", userInfo.getOrDefault("country", "US"),
            "message", "User registered successfully"
        );
        return ResponseEntity.status(201).body(response);
    }
}
