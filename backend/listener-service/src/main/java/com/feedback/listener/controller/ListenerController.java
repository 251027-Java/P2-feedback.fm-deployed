package com.feedback.listener.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.feedback.listener.dtos.ListenerDTO;
import com.feedback.listener.service.ListenerService;
import com.feedback.listener.service.SpotifyApiService;

@RestController
@RequestMapping("/api/users")
public class ListenerController {

    private final ListenerService listenerService;
    private final SpotifyApiService spotifyApiService;

    public ListenerController(ListenerService listenerService, SpotifyApiService spotifyApiService) {
        this.listenerService = listenerService;
        this.spotifyApiService = spotifyApiService;
    }

    // Get user profile by ID
    @GetMapping("/{id}")
    public ResponseEntity<ListenerDTO> getUserProfile(@PathVariable String id) {
        return listenerService.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Get complete dashboard data for a user
    @GetMapping("/{id}/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @PathVariable String id,
            @RequestHeader(value = "X-Spotify-Token", required = false) String spotifyToken) {
        Optional<ListenerDTO> listenerOpt = listenerService.getById(id);
        if (listenerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        ListenerDTO listener = listenerOpt.get();

        Map<String, Object> stats = new HashMap<>();
        
        // Use stored cumulative stats from database
        Long totalListeningTimeMs = listener.totalListeningTimeMs() != null ? listener.totalListeningTimeMs() : 0L;
        Integer totalSongsPlayed = listener.totalSongsPlayed() != null ? listener.totalSongsPlayed() : 0;
        
        System.out.println("[" + LocalDateTime.now() + "] Initial stats from DB - totalListeningTimeMs: " + totalListeningTimeMs + ", totalSongsPlayed: " + totalSongsPlayed);
        
        // Convert milliseconds to hours and minutes
        long totalMinutes = totalListeningTimeMs / 60000;
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        if (hours > 0) {
            stats.put("totalListeningTime", hours + " hours " + minutes + " minutes");
        } else if (totalMinutes > 0) {
            stats.put("totalListeningTime", minutes + " minutes");
        } else {
            stats.put("totalListeningTime", "0 minutes");
        }
        
        stats.put("songsPlayed", totalSongsPlayed);
        stats.put("currentStreak", 0); // Will calculate streak from recently played
        
        // Calculate streak from Spotify recently played data if token is provided
        // (We still use recently played for streak calculation as it's time-based)
        // Note: Spotify's recently played endpoint only returns the last 50 tracks maximum
        // Stats represent activity from those tracks - for comprehensive stats, we'd need database storage
        System.out.println("[" + LocalDateTime.now() + "] Checking Spotify token - token provided: " + (spotifyToken != null && !spotifyToken.isBlank()));
        if (spotifyToken != null && !spotifyToken.isBlank()) {
            try {
                System.out.println("[" + LocalDateTime.now() + "] Calling Spotify API to get recently played tracks...");
                // Get recently played to calculate stats (limit 50 is max Spotify allows)
                Map<String, Object> recentlyPlayed = spotifyApiService.getRecentlyPlayed(spotifyToken, 50);
                System.out.println("[" + LocalDateTime.now() + "] Fetching dashboard stats - recently played response: " + 
                    (recentlyPlayed != null ? "not null, keys: " + recentlyPlayed.keySet() : "null"));
                
                if (recentlyPlayed != null && recentlyPlayed.containsKey("items")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> items = (List<Map<String, Object>>) recentlyPlayed.get("items");
                    int itemCount = items != null ? items.size() : 0;
                    System.out.println("[" + LocalDateTime.now() + "] Number of recently played items: " + itemCount);
                    
                    if (items != null && !items.isEmpty()) {
                        // Calculate streak from listening days in recently played
                        Set<String> listeningDays = new HashSet<>();
                        
                        for (Map<String, Object> item : items) {
                            // Track listening days for streak calculation
                            Object playedAtObj = item.get("played_at");
                            if (playedAtObj instanceof String) {
                                try {
                                    Instant instant = Instant.parse((String) playedAtObj);
                                    LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                                    listeningDays.add(date.toString());
                                } catch (Exception e) {
                                    // Skip invalid dates
                                }
                            }
                        }
                        
                        // Calculate current streak (consecutive days including today)
                        LocalDate today = LocalDate.now();
                        int streak = 0;
                        LocalDate checkDate = today;
                        
                        // Check for consecutive days
                        while (listeningDays.contains(checkDate.toString())) {
                            streak++;
                            checkDate = checkDate.minusDays(1);
                            // Limit streak check to reasonable range (e.g., last 365 days)
                            if (checkDate.isBefore(today.minusDays(365))) {
                                break;
                            }
                        }
                        
                        stats.put("currentStreak", streak);
                        System.out.println("[" + LocalDateTime.now() + "] Listening days found: " + listeningDays.size() + " - " + listeningDays);
                        System.out.println("[" + LocalDateTime.now() + "] Calculated streak: " + streak + " days");
                        
                        // Also trigger sync to update cumulative stats with new plays
                        // This ensures stats are kept up-to-date when dashboard is accessed
                        System.out.println("[" + LocalDateTime.now() + "] About to call syncRecentlyPlayed for listener: " + id);
                        try {
                            spotifyApiService.syncRecentlyPlayed(spotifyToken, id);
                            System.out.println("[" + LocalDateTime.now() + "] syncRecentlyPlayed completed successfully");
                            
                            // Recalculate stats from all history records to ensure accuracy
                            // This handles cases where stats might be out of sync
                            spotifyApiService.recalculateStatsFromHistory(id);
                            
                            // Re-fetch listener to get updated stats
                            // Note: Since syncRecentlyPlayed is @Transactional, changes should be committed
                            listenerOpt = listenerService.getById(id);
                            if (listenerOpt.isPresent()) {
                                listener = listenerOpt.get();
                                Long updatedTotalTime = listener.totalListeningTimeMs() != null ? listener.totalListeningTimeMs() : 0L;
                                Integer updatedSongs = listener.totalSongsPlayed() != null ? listener.totalSongsPlayed() : 0;
                                
                                System.out.println("[" + LocalDateTime.now() + "] Stats after sync - totalListeningTimeMs: " + updatedTotalTime + ", totalSongsPlayed: " + updatedSongs);
                                
                                // Update stats with fresh values
                                long updatedMinutes = updatedTotalTime / 60000;
                                long updatedHours = updatedMinutes / 60;
                                long updatedMins = updatedMinutes % 60;
                                if (updatedHours > 0) {
                                    stats.put("totalListeningTime", updatedHours + " hours " + updatedMins + " minutes");
                                } else if (updatedMinutes > 0) {
                                    stats.put("totalListeningTime", updatedMins + " minutes");
                                } else {
                                    stats.put("totalListeningTime", "0 minutes");
                                }
                                stats.put("songsPlayed", updatedSongs);
                            } else {
                                System.err.println("[" + LocalDateTime.now() + "] Listener not found after sync for ID: " + id);
                            }
                        } catch (Exception syncException) {
                            System.err.println("[" + LocalDateTime.now() + "] Error syncing recently played: " + syncException.getMessage());
                            syncException.printStackTrace();
                            
                            // Even if sync fails, try to recalculate from existing history
                            try {
                                spotifyApiService.recalculateStatsFromHistory(id);
                                listenerOpt = listenerService.getById(id);
                                if (listenerOpt.isPresent()) {
                                    listener = listenerOpt.get();
                                    Long recalculatedTime = listener.totalListeningTimeMs() != null ? listener.totalListeningTimeMs() : 0L;
                                    Integer recalculatedSongs = listener.totalSongsPlayed() != null ? listener.totalSongsPlayed() : 0;
                                    
                                    long recalcMinutes = recalculatedTime / 60000;
                                    long recalcHours = recalcMinutes / 60;
                                    long recalcMins = recalcMinutes % 60;
                                    if (recalcHours > 0) {
                                        stats.put("totalListeningTime", recalcHours + " hours " + recalcMins + " minutes");
                                    } else if (recalcMinutes > 0) {
                                        stats.put("totalListeningTime", recalcMins + " minutes");
                                    }
                                    stats.put("songsPlayed", recalculatedSongs);
                                }
                            } catch (Exception recalcException) {
                                System.err.println("[" + LocalDateTime.now() + "] Error recalculating stats: " + recalcException.getMessage());
                            }
                        }
                    } else {
                        System.out.println("[" + LocalDateTime.now() + "] No items found in recently played - will still try to sync");
                        // Even if no items, try to sync (might have new data)
                        try {
                            System.out.println("[" + LocalDateTime.now() + "] Calling sync even though items list is empty");
                            spotifyApiService.syncRecentlyPlayed(spotifyToken, id);
                            spotifyApiService.recalculateStatsFromHistory(id);
                        } catch (Exception syncException) {
                            System.err.println("[" + LocalDateTime.now() + "] Error syncing with empty items: " + syncException.getMessage());
                        }
                    }
                } else {
                    System.out.println("[" + LocalDateTime.now() + "] Recently played response missing 'items' key or is null - will still try to sync");
                    // Try to sync anyway - might have data
                    try {
                        System.out.println("[" + LocalDateTime.now() + "] Calling sync even though response is missing items");
                        spotifyApiService.syncRecentlyPlayed(spotifyToken, id);
                        spotifyApiService.recalculateStatsFromHistory(id);
                    } catch (Exception syncException) {
                        System.err.println("[" + LocalDateTime.now() + "] Error syncing with missing items: " + syncException.getMessage());
                    }
                }
            } catch (Exception e) {
                // Keep default stats if calculation fails
                System.err.println("[" + LocalDateTime.now() + "] Error calculating dashboard stats: " + e.getMessage());
                e.printStackTrace();
                // Still try to sync even if there's an error
                try {
                    System.out.println("[" + LocalDateTime.now() + "] Attempting sync after error");
                    spotifyApiService.syncRecentlyPlayed(spotifyToken, id);
                    spotifyApiService.recalculateStatsFromHistory(id);
                } catch (Exception syncException) {
                    System.err.println("[" + LocalDateTime.now() + "] Error syncing after exception: " + syncException.getMessage());
                }
            }
        } else {
            System.out.println("[" + LocalDateTime.now() + "] No Spotify token provided for dashboard stats");
            // Even without token, recalculate stats from existing history to ensure accuracy
            try {
                spotifyApiService.recalculateStatsFromHistory(id);
                listenerOpt = listenerService.getById(id);
                if (listenerOpt.isPresent()) {
                    listener = listenerOpt.get();
                    Long recalculatedTime = listener.totalListeningTimeMs() != null ? listener.totalListeningTimeMs() : 0L;
                    Integer recalculatedSongs = listener.totalSongsPlayed() != null ? listener.totalSongsPlayed() : 0;
                    
                    long recalcMinutes = recalculatedTime / 60000;
                    long recalcHours = recalcMinutes / 60;
                    long recalcMins = recalcMinutes % 60;
                    if (recalcHours > 0) {
                        stats.put("totalListeningTime", recalcHours + " hours " + recalcMins + " minutes");
                    } else if (recalcMinutes > 0) {
                        stats.put("totalListeningTime", recalcMins + " minutes");
                    } else {
                        stats.put("totalListeningTime", "0 minutes");
                    }
                    stats.put("songsPlayed", recalculatedSongs);
                    
                    System.out.println("[" + LocalDateTime.now() + "] Recalculated stats without token - totalListeningTimeMs: " + recalculatedTime + ", totalSongsPlayed: " + recalculatedSongs);
                }
            } catch (Exception recalcException) {
                System.err.println("[" + LocalDateTime.now() + "] Error recalculating stats without token: " + recalcException.getMessage());
            }
        }
        
        List<Map<String, Object>> topArtistsData = List.of();
        List<Map<String, Object>> topSongsData = List.of();
        
        // Get top artists and songs from Spotify API if token is provided
        if (spotifyToken != null && !spotifyToken.isBlank()) {
            try {
                // Get top artists (short_term = last 4 weeks / approximately last week of listening)
                Map<String, Object> artistsResponse = spotifyApiService.getTopArtists(spotifyToken, "short_term");
                if (artistsResponse != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> artistsItems = (List<Map<String, Object>>) artistsResponse.get("items");
                    if (artistsItems != null) {
                        topArtistsData = artistsItems.stream()
                            .limit(5)
                            .map(item -> {
                                Map<String, Object> artistMap = new HashMap<>();
                                artistMap.put("id", item.get("id"));
                                artistMap.put("name", item.get("name"));
                                @SuppressWarnings("unchecked")
                                Map<String, Object> externalUrls = (Map<String, Object>) item.get("external_urls");
                                if (externalUrls != null) {
                                    artistMap.put("href", externalUrls.get("spotify"));
                                }
                                // Get artist image
                                @SuppressWarnings("unchecked")
                                List<Map<String, Object>> images = (List<Map<String, Object>>) item.get("images");
                                if (images != null && !images.isEmpty()) {
                                    artistMap.put("image", images.get(0).get("url"));
                                }
                                return artistMap;
                            })
                            .collect(Collectors.toList());
                    }
                }
                
                // Get top songs (short_term = last 4 weeks / approximately last week of listening)
                Map<String, Object> tracksResponse = spotifyApiService.getTopTracks(spotifyToken, "short_term");
                if (tracksResponse != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> tracksItems = (List<Map<String, Object>>) tracksResponse.get("items");
                    if (tracksItems != null) {
                        topSongsData = tracksItems.stream()
                            .limit(5)
                            .map(item -> {
                                Map<String, Object> songMap = new HashMap<>();
                                songMap.put("id", item.get("id"));
                                songMap.put("name", item.get("name"));
                                
                                // Get artist name
                                @SuppressWarnings("unchecked")
                                List<Map<String, Object>> artists = (List<Map<String, Object>>) item.get("artists");
                                String artistName = "Unknown Artist";
                                if (artists != null && !artists.isEmpty()) {
                                    artistName = (String) artists.get(0).get("name");
                                }
                                songMap.put("artistName", artistName);
                                
                                @SuppressWarnings("unchecked")
                                Map<String, Object> externalUrls = (Map<String, Object>) item.get("external_urls");
                                if (externalUrls != null) {
                                    songMap.put("href", externalUrls.get("spotify"));
                                }
                                
                                // Get album image
                                @SuppressWarnings("unchecked")
                                Map<String, Object> albumData = (Map<String, Object>) item.get("album");
                                if (albumData != null) {
                                    @SuppressWarnings("unchecked")
                                    List<Map<String, Object>> albumImages = (List<Map<String, Object>>) albumData.get("images");
                                    if (albumImages != null && !albumImages.isEmpty()) {
                                        songMap.put("image", albumImages.get(0).get("url"));
                                    }
                                }
                                return songMap;
                            })
                            .collect(Collectors.toList());
                    }
                }
            } catch (Exception e) {
                // If Spotify API fails, return empty lists
                // Stats will still be returned
            }
        }
        
        // Get user profile image from Spotify if token is provided
        String profileImage = null;
        if (spotifyToken != null && !spotifyToken.isBlank()) {
            try {
                Map<String, Object> spotifyUser = spotifyApiService.getCurrentUser(spotifyToken);
                if (spotifyUser != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> images = (List<Map<String, Object>>) spotifyUser.get("images");
                    if (images != null && !images.isEmpty()) {
                        // Find the largest image (usually first, but check height/width)
                        for (Map<String, Object> image : images) {
                            Object urlObj = image.get("url");
                            if (urlObj instanceof String) {
                                profileImage = (String) urlObj;
                                // Use the first image (Spotify usually returns largest first)
                                break;
                            }
                        }
                        System.out.println("Profile image URL: " + profileImage);
                    } else {
                        System.out.println("No images found in Spotify user profile");
                    }
                } else {
                    System.out.println("Spotify user profile is null");
                }
            } catch (Exception e) {
                // If profile image fetch fails, continue without it
                System.err.println("Error fetching profile image: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No Spotify token provided for profile image");
        }
        
        // Ensure stats object always exists with all required fields
        if (!stats.containsKey("totalListeningTime")) {
            stats.put("totalListeningTime", "0 minutes");
        }
        if (!stats.containsKey("songsPlayed")) {
            stats.put("songsPlayed", 0);
        }
        if (!stats.containsKey("currentStreak")) {
            stats.put("currentStreak", 0);
        }
        
        System.out.println("[" + LocalDateTime.now() + "] Returning dashboard with stats: " + stats);
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("userId", listener.listenerId());
        dashboard.put("username", listener.displayName() != null ? listener.displayName() : "");
        dashboard.put("email", listener.email() != null ? listener.email() : "");
        dashboard.put("profileImage", profileImage);
        dashboard.put("stats", stats);
        dashboard.put("topArtists", topArtistsData);
        dashboard.put("topSongs", topSongsData);
        return ResponseEntity.ok(dashboard);
    }

    // Get user statistics summary
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String id) {
        Optional<ListenerDTO> listenerOpt = listenerService.getById(id);
        if (listenerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalListeningTime", "0 hours");
        stats.put("songsPlayed", 0);
        stats.put("songsPlayedToday", 0);
        stats.put("currentStreak", 0);
        stats.put("topGenre", "N/A");
        return ResponseEntity.ok(stats);
    }

    // Update user profile
    @PutMapping("/{id}")
    public ResponseEntity<ListenerDTO> updateUser(@PathVariable String id, @RequestBody ListenerDTO listenerDTO) {
        ListenerDTO updated = listenerService.update(id, listenerDTO);
        return ResponseEntity.ok(updated);
    }

    // Delete user account
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        listenerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Simple login endpoint. Later fix
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(400).body(Map.of("error", "Email is required"));
        }
        
        Optional<ListenerDTO> listenerOpt = listenerService.findByEmail(email);
        if (listenerOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        
        ListenerDTO listener = listenerOpt.get();
        
        Map<String, Object> response = new HashMap<>();
        response.put("listenerId", listener.listenerId());
        response.put("displayName", listener.displayName());
        response.put("email", listener.email());
        response.put("country", listener.country());
        response.put("token", "mock-jwt-token-12345");
        response.put("message", "Login successful");
        return ResponseEntity.ok(response);
    }

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<ListenerDTO> register(@RequestBody ListenerDTO listenerDTO) {
        ListenerDTO created = listenerService.create(listenerDTO);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/find-by-id/{id}")
    public ListenerDTO findById(@PathVariable String id) {
        return listenerService.getById(id).orElse(null);
    }   

    @GetMapping("/exists/{id}")
    public boolean existsById(@PathVariable String id) {
        boolean exists = listenerService.existsById(id);
        return exists;
    }
}