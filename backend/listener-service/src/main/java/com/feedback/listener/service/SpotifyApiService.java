package com.feedback.listener.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("spotify-integration-service")
public interface SpotifyApiService {

    @GetMapping("/api/api/top-tracks")
    public Map<String, Object> getTopTracks(@RequestParam String accessToken, @RequestParam String timeRange);

    @GetMapping("/api/api/top-artists") 
    public Map<String, Object> getTopArtists(@RequestParam String accessToken, @RequestParam String timeRange);

    @GetMapping("/api/api/recently-played")
    public Map<String, Object> getRecentlyPlayed(@RequestParam String accessToken, @RequestParam int limit);

    @GetMapping("/api/api/current-user")
    public Map<String, Object> getCurrentUser(@RequestParam String accessToken);

    @PostMapping("/api/sync/recalculate-stats/{listenerId}")
    public void recalculateStatsFromHistory(@PathVariable String listenerId);

    @PostMapping("/api/sync/sync-recently-played/{listenerId}")
    public void syncRecentlyPlayed(@RequestBody String accessToken, @PathVariable String listenerId);


}

