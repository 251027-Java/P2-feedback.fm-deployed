package com.feedback.playlist.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("spotify-integration-service")
public interface SpotifyApiService {
    
    @GetMapping("/api/user/playlists")
    public Map<String, Object> getUserPlaylists(@RequestBody String accessToken, @RequestParam int limit, @RequestParam int offset);

    @GetMapping("/api/user/playlist-tracks")
    public Map<String, Object> getPlaylistTracks(@RequestBody String accessToken, @RequestParam String playlistId);
}

