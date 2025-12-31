package com.feedback.spotify.controller;

import com.feedback.spotify.security.JwtUtil;
import com.feedback.spotify.service.ListenerService; // Need to fix
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
@RequestMapping("/api/sync")
public class SpotifySyncController {

    private final SpotifySyncService syncService;

    public SpotifySyncController(SpotifySyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping("/recalculate-stats/{listenerId}")
    public void recalculateStatsFromHistory(@PathVariable String listenerId) {
        syncService.recalculateStatsFromHistory(listenerId);
    }

    @PostMapping("/sync-recently-played/{listenerId}")
    public void syncRecentlyPlayed(@RequestBody String accessToken, @PathVariable String listenerId) {
        syncService.syncRecentlyPlayed(accessToken, listenerId);
    }
}