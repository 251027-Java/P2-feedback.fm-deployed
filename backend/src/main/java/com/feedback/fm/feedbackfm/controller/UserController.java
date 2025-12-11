package com.feedback.fm.feedbackfm.controller;

import com.feedback.fm.feedbackfm.dtos.ListenerDTO;
import com.feedback.fm.feedbackfm.service.ListenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final ListenerService listenerService;

    @Autowired
    public UserController(ListenerService listenerService) {
        this.listenerService = listenerService;
    }

    // Get user profile by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable String id) {
        Optional<ListenerDTO> listenerOpt = listenerService.getById(id);
        if (listenerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        ListenerDTO listener = listenerOpt.get();
        Map<String, Object> user = new HashMap<>();
        user.put("userId", listener.listenerId());
        user.put("username", listener.displayName() != null ? listener.displayName() : "");
        user.put("email", listener.email() != null ? listener.email() : "");
        user.put("spotifyUserId", listener.listenerId()); // Using listenerId as spotifyUserId
        return ResponseEntity.ok(user);
    }

    // Get complete dashboard data for a user
    @GetMapping("/{id}/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@PathVariable String id) {
        Optional<ListenerDTO> listenerOpt = listenerService.getById(id);
        if (listenerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        ListenerDTO listener = listenerOpt.get();
        // Note: Statistics would come from HistoryService (to be implemented)
        // For now, returning basic listener info
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalListeningTime", "0 hours"); // TODO: Calculate from HistoryService
        stats.put("songsPlayed", 0); // TODO: Calculate from HistoryService
        stats.put("currentStreak", 0); // TODO: Calculate from HistoryService
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("userId", listener.listenerId());
        dashboard.put("username", listener.displayName() != null ? listener.displayName() : "");
        dashboard.put("email", listener.email() != null ? listener.email() : "");
        dashboard.put("stats", stats);
        dashboard.put("topArtists", List.of()); // TODO: Get from HistoryService
        dashboard.put("topSongs", List.of()); // TODO: Get from HistoryService
        return ResponseEntity.ok(dashboard);
    }

    // Get user statistics summary
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String id) {
        Optional<ListenerDTO> listenerOpt = listenerService.getById(id);
        if (listenerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Note: Statistics would come from HistoryService (to be implemented)
        // For now, returning placeholder stats
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalListeningTime", "0 hours"); // TODO: Calculate from HistoryService
        stats.put("songsPlayed", 0); // TODO: Calculate from HistoryService
        stats.put("songsPlayedToday", 0); // TODO: Calculate from HistoryService
        stats.put("currentStreak", 0); // TODO: Calculate from HistoryService
        stats.put("topGenre", "N/A"); // TODO: Calculate from HistoryService
        return ResponseEntity.ok(stats);
    }

    // Update user profile
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        // Get existing listener to preserve unchanged fields
        Optional<ListenerDTO> existingOpt = listenerService.getById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        ListenerDTO existing = existingOpt.get();
        
        // Build updated DTO from request body, using existing values as defaults
        // Handle null values properly - if key exists but value is null, use existing value
        String displayName = updates.containsKey("username") 
            ? (updates.get("username") != null ? (String) updates.get("username") : existing.displayName())
            : existing.displayName();
        String email = updates.containsKey("email")
            ? (updates.get("email") != null ? (String) updates.get("email") : existing.email())
            : existing.email();
        String country = updates.containsKey("country")
            ? (updates.get("country") != null ? (String) updates.get("country") : existing.country())
            : existing.country();
        String href = updates.containsKey("href")
            ? (updates.get("href") != null ? (String) updates.get("href") : existing.href())
            : existing.href();
        
        ListenerDTO updatedDto = new ListenerDTO(
            existing.listenerId(), // ID cannot be changed
            displayName,
            email,
            country,
            href
        );
        
        ListenerDTO updated = listenerService.update(id, updatedDto);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", updated.listenerId());
        response.put("username", updated.displayName() != null ? updated.displayName() : "");
        response.put("email", updated.email() != null ? updated.email() : "");
        response.put("message", "User updated successfully");
        return ResponseEntity.ok(response);
    }

    // Delete user account
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String id) {
        // Check if listener exists before attempting delete
        Optional<ListenerDTO> listenerOpt = listenerService.getById(id);
        if (listenerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        listenerService.delete(id);
        Map<String, String> response = Map.of("message", "User " + id + " deleted successfully");
        return ResponseEntity.ok(response);
    }

    // Simple login endpoint
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        // TODO: Implement proper authentication/authorization (Phase 5)
        // For now, checking if user exists by email
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(400).body(Map.of("error", "Email is required"));
        }
        
        Optional<ListenerDTO> listenerOpt = listenerService.findByEmail(email);
        if (listenerOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        
        ListenerDTO listener = listenerOpt.get();
        // TODO: Validate password when authentication is implemented
        // For now, just return user info if email exists
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", listener.listenerId());
        response.put("username", listener.displayName() != null ? listener.displayName() : "");
        response.put("email", listener.email() != null ? listener.email() : "");
        response.put("token", "mock-jwt-token-12345"); // TODO: Generate real JWT token
        response.put("message", "Login successful");
        return ResponseEntity.ok(response);
    }

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> userInfo) {
        // Validate required fields
        String email = userInfo.get("email");
        String displayName = userInfo.get("username"); // username maps to displayName
        String listenerId = userInfo.get("listenerId"); // Spotify user ID
        
        if (listenerId == null || listenerId.isBlank()) {
            return ResponseEntity.status(400).body(Map.of("error", "listenerId is required"));
        }
        
        // Create ListenerDTO from request
        ListenerDTO newListener = new ListenerDTO(
            listenerId,
            displayName,
            email,
            userInfo.get("country"),
            userInfo.get("href")
        );
        
        try {
            ListenerDTO created = listenerService.create(newListener);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", created.listenerId());
            response.put("username", created.displayName() != null ? created.displayName() : "");
            response.put("email", created.email() != null ? created.email() : "");
            response.put("message", "User registered successfully");
            return ResponseEntity.status(201).body(response);
        } catch (org.springframework.web.server.ResponseStatusException e) {
            // Re-throw the exception to return proper error response
            throw e;
        }
    }
}
