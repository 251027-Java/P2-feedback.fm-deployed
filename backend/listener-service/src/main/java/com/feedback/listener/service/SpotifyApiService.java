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

    @GetMapping("/api/user/top-tracks")
    public Map<String, Object> getTopTracks(@RequestBody String accessToken, @RequestParam String timeRange);

    @GetMapping("/api/user/top-artists") 
    public Map<String, Object> getTopArtists(@RequestBody String accessToken, @RequestParam String timeRange);

    @GetMapping("/api/user/recently-played")
    public Map<String, Object> getRecentlyPlayed(@RequestBody String accessToken, @RequestParam int limit);

    @GetMapping("/api/user/current-user")
    public Map<String, Object> getCurrentUser(@RequestBody String accessToken);

    @PostMapping("/api/sync/recalculate-stats/{listenerId}")
    public void recalculateStatsFromHistory(@PathVariable String listenerId);

    @PostMapping("/api/sync/sync-recently-played/{listenerId}")
    public void syncRecentlyPlayed(@RequestBody String accessToken, @PathVariable String listenerId);


}

