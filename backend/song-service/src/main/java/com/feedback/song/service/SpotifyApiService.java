package com.feedback.song.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("spotify-integration-service")
public interface SpotifyApiService {
    
    @GetMapping("/api/user/currently-playing")
    public Map<String, Object> getCurrentlyPlaying(@RequestBody String accessToken);

    @GetMapping("/api/user/top-tracks")
    public Map<String, Object> getTopTracks(@RequestBody String accessToken, @RequestParam String timeRange);

}

