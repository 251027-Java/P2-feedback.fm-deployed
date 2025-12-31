package com.feedback.artist.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("spotify-integration-service")
public interface SpotifyApiService {
    
    @GetMapping("/api/user/top-artists") 
    public Map<String, Object> getTopArtists(@RequestBody String accessToken, @RequestParam String timeRange);
}

