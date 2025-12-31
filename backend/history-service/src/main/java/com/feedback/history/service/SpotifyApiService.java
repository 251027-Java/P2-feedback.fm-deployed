package com.feedback.history.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("spotify-integration-service")
public interface SpotifyApiService {

    @GetMapping("/api/user/recently-played")
    public Map<String, Object> getRecentlyPlayed(@RequestBody String accessToken, @RequestParam int limit);
}

