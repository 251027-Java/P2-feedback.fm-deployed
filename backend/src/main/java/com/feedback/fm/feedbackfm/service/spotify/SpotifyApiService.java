package com.feedback.fm.feedbackfm.service.spotify;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.feedback.fm.feedbackfm.exception.AuthenticationException;
import com.feedback.fm.feedbackfm.exception.SpotifyApiException;

@Service
public class SpotifyApiService {
    
    @Value("${spotify.api.base.url}")
    private String apiBaseUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public Map<String, Object> getCurrentUser(String accessToken) {
        String url = apiBaseUrl + "/me";
        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new SpotifyApiException("Failed to get current user");
            }
        } catch (RestClientException e) {
            throw new SpotifyApiException("Error getting current user: " + e.getMessage(), e);
        }
    }
    
    public Map<String, Object> getCurrentlyPlaying(String accessToken) {
        String url = apiBaseUrl + "/me/player/currently-playing";
        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                return null; // No song currently playing (204 No Content)
            }
        } catch (RestClientException e) {
            return null; // No song currently playing or error
        }
    }
    
    public Map<String, Object> getTopArtists(String accessToken, String timeRange) {
        if (timeRange == null || timeRange.isBlank()) {
            timeRange = "medium_term";
        }
        String url = apiBaseUrl + "/me/top/artists?time_range=" + timeRange + "&limit=50";
        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new SpotifyApiException("Failed to get top artists");
            }
        } catch (RestClientException e) {
            throw new SpotifyApiException("Error getting top artists: " + e.getMessage(), e);
        }
    }
    
    public Map<String, Object> getTopTracks(String accessToken, String timeRange) {
        if (timeRange == null || timeRange.isBlank()) {
            timeRange = "medium_term";
        }
        String url = apiBaseUrl + "/me/top/tracks?time_range=" + timeRange + "&limit=50";
        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new SpotifyApiException("Failed to get top tracks");
            }
        } catch (RestClientException e) {
            throw new SpotifyApiException("Error getting top tracks: " + e.getMessage(), e);
        }
    }
    
    public Map<String, Object> getRecentlyPlayed(String accessToken, int limit) {
        if (limit <= 0 || limit > 50) {
            limit = 50; // Spotify API max is 50
        }
        String url = apiBaseUrl + "/me/player/recently-played?limit=" + limit;
        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );
            // 204 No Content means no recently played tracks
            if (response.getStatusCode().value() == 204) {
                return new java.util.HashMap<>();
            }
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                // Return empty map instead of throwing exception
                return new java.util.HashMap<>();
            }
        } catch (RestClientException e) {
            // Log but return empty map instead of throwing
            System.err.println("Error getting recently played: " + e.getMessage());
            return new java.util.HashMap<>();
        }
    }
    
    public Map<String, Object> getUserPlaylists(String accessToken, int limit, int offset) {
        if (limit <= 0 || limit > 50) {
            limit = 50;
        }
        if (offset < 0) {
            offset = 0;
        }
        String url = apiBaseUrl + "/me/playlists?limit=" + limit + "&offset=" + offset;
        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new SpotifyApiException("Failed to get user playlists");
            }
        } catch (RestClientException e) {
            throw new SpotifyApiException("Error getting user playlists: " + e.getMessage(), e);
        }
    }
    
    public Map<String, Object> getPlaylistTracks(String accessToken, String playlistId) {
        String url = apiBaseUrl + "/playlists/" + playlistId + "/tracks";
        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new SpotifyApiException("Failed to get playlist tracks");
            }
        } catch (RestClientException e) {
            throw new SpotifyApiException("Error getting playlist tracks: " + e.getMessage(), e);
        }
    }
    
    private HttpHeaders createHeaders(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new AuthenticationException("Access token is required");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}

