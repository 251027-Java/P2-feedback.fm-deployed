package com.feedback.spotify.controller;

import com.feedback.spotify.service.ListenerService;
import com.feedback.spotify.service.SpotifyApiService;
import com.feedback.spotify.service.SpotifyAuthService;
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
@RequestMapping("/api/api")
public class SpotifyApiController {

    private final SpotifyApiService apiService;
    
    public SpotifyApiController(SpotifyApiService apiService) {
        this.apiService = apiService;
    }


    @GetMapping("/current-user")
    public Map<String, Object> getCurrentUser(@RequestParam String accessToken) {
        return apiService.getCurrentUser(accessToken);
    }

    @GetMapping("/currently-playing")
    public Map<String, Object> getCurrentlyPlaying(@RequestParam String accessToken) {
        return apiService.getCurrentlyPlaying(accessToken);
    }

    @GetMapping("/top-tracks")
    public Map<String, Object> getTopTracks(@RequestParam String accessToken, @RequestParam String timeRange) {
        return apiService.getTopTracks(accessToken, timeRange);
    }

    @GetMapping("/playlists")
    public Map<String, Object> getUserPlaylists(@RequestParam String accessToken, @RequestParam int limit, @RequestParam int offset) {
        return apiService.getUserPlaylists(accessToken, limit, offset);
    }

    @GetMapping("/playlist-tracks")
    public Map<String, Object> getPlaylistTracks(@RequestParam String accessToken, @RequestParam String playlistId) {
        return apiService.getPlaylistTracks(accessToken, playlistId);
    }

    @GetMapping("/top-artists")
    public Map<String, Object> getTopArtists(@RequestParam String accessToken, @RequestParam String timeRange) {
        return apiService.getTopArtists(accessToken, timeRange);
    }

    @GetMapping("/recently-played")
    public Map<String, Object> getRecentlyPlayed(@RequestParam String accessToken, @RequestParam int limit) {
        return apiService.getRecentlyPlayed(accessToken, limit);
    }
}