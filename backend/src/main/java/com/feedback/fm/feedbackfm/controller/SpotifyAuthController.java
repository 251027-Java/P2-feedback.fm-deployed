package com.feedback.fm.feedbackfm.controller;

import com.feedback.fm.feedbackfm.service.ListenerService;
import com.feedback.fm.feedbackfm.service.spotify.SpotifyApiService;
import com.feedback.fm.feedbackfm.service.spotify.SpotifyAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class SpotifyAuthController {
    
    private final SpotifyAuthService authService;
    private final SpotifyApiService apiService;
    private final ListenerService listenerService;
    
    @Autowired
    public SpotifyAuthController(SpotifyAuthService authService, SpotifyApiService apiService, ListenerService listenerService) {
        this.authService = authService;
        this.apiService = apiService;
        this.listenerService = listenerService;
    }
    
    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> getAuthUrl() {
        String authUrl = authService.getAuthorizationUrl();
        Map<String, String> response = new HashMap<>();
        response.put("authUrl", authUrl);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/callback")
    public ResponseEntity<Map<String, Object>> handleCallback(@RequestParam String code) {
        try {
            // Exchange code for access token
            Map<String, Object> tokenResponse = authService.exchangeCodeForToken(code);
            String accessToken = (String) tokenResponse.get("access_token");
            String refreshToken = (String) tokenResponse.get("refresh_token");
            
            if (accessToken == null) {
                return ResponseEntity.status(400).body(Map.of("error", "Failed to get access token"));
            }
            
            // Get user profile from Spotify
            Map<String, Object> userProfile = apiService.getCurrentUser(accessToken);
            
            // Create or update listener in database
            String spotifyId = (String) userProfile.get("id");
            String displayName = (String) userProfile.get("display_name");
            String email = (String) userProfile.get("email");
            String country = (String) userProfile.get("country");
            Map<String, Object> externalUrls = (Map<String, Object>) userProfile.get("external_urls");
            String href = externalUrls != null ? (String) externalUrls.get("spotify") : null;
            
            // Check if listener exists, create or update
            var existingListener = listenerService.getById(spotifyId);
            if (existingListener.isEmpty()) {
                // Create new listener
                listenerService.create(new com.feedback.fm.feedbackfm.dtos.ListenerDTO(
                    spotifyId,
                    displayName,
                    email,
                    country,
                    href
                ));
            } else {
                // Update existing listener
                listenerService.update(spotifyId, new com.feedback.fm.feedbackfm.dtos.ListenerDTO(
                    spotifyId,
                    displayName,
                    email,
                    country,
                    href
                ));
            }
            
            // Return response with tokens and user info
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            if (refreshToken != null) {
                response.put("refreshToken", refreshToken);
            }
            response.put("user", userProfile);
            response.put("listenerId", spotifyId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Authentication failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(400).body(Map.of("error", "Refresh token is required"));
        }
        
        try {
            Map<String, Object> tokenResponse = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token refresh failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }
}
