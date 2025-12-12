package com.feedback.fm.feedbackfm.controller;

import com.feedback.fm.feedbackfm.dtos.ListenerDTO;
import com.feedback.fm.feedbackfm.service.ListenerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class ListenerController {

    private final ListenerService listenerService;

    public ListenerController(ListenerService listenerService) {
        this.listenerService = listenerService;
    }

    // Get user profile by ID
    @GetMapping("/{id}")
    public ResponseEntity<ListenerDTO> getUserProfile(@PathVariable String id) {
        return listenerService.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Get complete dashboard data for a user
    @GetMapping("/{id}/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@PathVariable String id) {
        Optional<ListenerDTO> listenerOpt = listenerService.getById(id);
        if (listenerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        ListenerDTO listener = listenerOpt.get();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalListeningTime", "0 hours");
        stats.put("songsPlayed", 0);
        stats.put("currentStreak", 0);
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("userId", listener.listenerId());
        dashboard.put("username", listener.displayName() != null ? listener.displayName() : "");
        dashboard.put("email", listener.email() != null ? listener.email() : "");
        dashboard.put("stats", stats);
        dashboard.put("topArtists", List.of());
        dashboard.put("topSongs", List.of());
        return ResponseEntity.ok(dashboard);
    }

    // Get user statistics summary
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String id) {
        Optional<ListenerDTO> listenerOpt = listenerService.getById(id);
        if (listenerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalListeningTime", "0 hours");
        stats.put("songsPlayed", 0);
        stats.put("songsPlayedToday", 0);
        stats.put("currentStreak", 0);
        stats.put("topGenre", "N/A");
        return ResponseEntity.ok(stats);
    }

    // Update user profile
    @PutMapping("/{id}")
    public ResponseEntity<ListenerDTO> updateUser(@PathVariable String id, @RequestBody ListenerDTO listenerDTO) {
        ListenerDTO updated = listenerService.update(id, listenerDTO);
        return ResponseEntity.ok(updated);
    }

    // Delete user account
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        listenerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Simple login endpoint. Later fix
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(400).body(Map.of("error", "Email is required"));
        }
        
        Optional<ListenerDTO> listenerOpt = listenerService.findByEmail(email);
        if (listenerOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        
        ListenerDTO listener = listenerOpt.get();
        
        Map<String, Object> response = new HashMap<>();
        response.put("listenerId", listener.listenerId());
        response.put("displayName", listener.displayName());
        response.put("email", listener.email());
        response.put("country", listener.country());
        response.put("token", "mock-jwt-token-12345");
        response.put("message", "Login successful");
        return ResponseEntity.ok(response);
    }

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<ListenerDTO> register(@RequestBody ListenerDTO listenerDTO) {
        ListenerDTO created = listenerService.create(listenerDTO);
        return ResponseEntity.status(201).body(created);
    }
}
