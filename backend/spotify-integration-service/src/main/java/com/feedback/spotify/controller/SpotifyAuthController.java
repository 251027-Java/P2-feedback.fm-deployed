package com.feedback.spotify.controller;

import com.feedback.spotify.security.JwtUtil;
import com.feedback.spotify.service.ListenerService;
import com.feedback.spotify.service.SpotifyApiService;
import com.feedback.spotify.service.SpotifyAuthService;
import com.feedback.spotify.dtos.ListenerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class SpotifyAuthController {

    private final SpotifyAuthService authService;
    private final SpotifyApiService apiService;
    private final ListenerService listenerService;
    private final JwtUtil jwtUtil;
    
    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    @Autowired
    public SpotifyAuthController(SpotifyAuthService authService, SpotifyApiService apiService, 
                                 ListenerService listenerService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.apiService = apiService;
        this.listenerService = listenerService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> getAuthUrl() {
        System.out.println("Received request for Spotify authorization URL");
        String authUrl = authService.getAuthorizationUrl();
        System.out.println("Authorization URL: " + authUrl);
        Map<String, String> response = new HashMap<>();
        response.put("authUrl", authUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/callback")
    public RedirectView handleCallback(@RequestParam String code) {
        try {
            // Exchange code for access token
            Map<String, Object> tokenResponse = authService.exchangeCodeForToken(code);
            String accessToken = (String) tokenResponse.get("access_token");
            String refreshToken = (String) tokenResponse.get("refresh_token");

            if (accessToken == null) {
                // Redirect to frontend with error
                String errorUrl = frontendUrl + "?error=" + URLEncoder.encode("Failed to get access token", StandardCharsets.UTF_8);
                return new RedirectView(errorUrl);
            }
            // System.out.println("Access token: " + accessToken);
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
            var existingListener = listenerService.findById(spotifyId);
            if (existingListener == null) {
                // Create new listener
                listenerService.register(new ListenerDTO(
                        spotifyId,
                        displayName,
                        email,
                        country,
                        href));
            } else {
                // Update existing listener
                listenerService.updateUser(spotifyId, new ListenerDTO(
                        spotifyId,
                        displayName,
                        email,
                        country,
                        href));
            }
            
            // Generate JWT token for API authentication
            String jwtToken = jwtUtil.generateToken(spotifyId);
            
            // Redirect to frontend with token, code, and Spotify access token in URL
            // Frontend will extract the code and call the API to get the full response
            String redirectUrl = frontendUrl + "/?code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
                    "&token=" + URLEncoder.encode(jwtToken, StandardCharsets.UTF_8) +
                    "&listenerId=" + URLEncoder.encode(spotifyId, StandardCharsets.UTF_8) +
                    "&spotifyToken=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
            
            return new RedirectView(redirectUrl);
        } catch (Exception e) {
            // Redirect to frontend with error
            try {
                String errorUrl = frontendUrl + "?error=" + URLEncoder.encode("Authentication failed: " + e.getMessage(), StandardCharsets.UTF_8);
                return new RedirectView(errorUrl);
            } catch (Exception ex) {
                // Fallback if encoding fails
                return new RedirectView(frontendUrl + "?error=Authentication failed");
            }
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