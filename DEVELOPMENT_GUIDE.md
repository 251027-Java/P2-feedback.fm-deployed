# Development Guide: feedback.fm

A comprehensive step-by-step guide for developing a full-stack Spotify statistics application similar to Last.fm.

## Table of Contents
1. [Project Overview](#project-overview)
2. [Phase 1: Database Setup & Configuration](#phase-1-database-setup--configuration)
3. [Phase 2: Backend Foundation](#phase-2-backend-foundation)
4. [Phase 3: Spotify API Integration](#phase-3-spotify-api-integration)
5. [Phase 4: Service Layer Implementation](#phase-4-service-layer-implementation)
6. [Phase 5: Authentication & Authorization](#phase-5-authentication--authorization)
7. [Phase 6: Exception Handling](#phase-6-exception-handling)
8. [Phase 7: Testing](#phase-7-testing)
9. [Phase 8: Frontend Development](#phase-8-frontend-development)
10. [Phase 9: Documentation](#phase-9-documentation)
11. [Phase 10: Git Workflow & Best Practices](#phase-10-git-workflow--best-practices)

---

## Project Overview

### Requirements Checklist
- ✅ **Database**: PostgreSQL in Docker, 3NF, 5+ tables, 1+ Many-to-Many relationship
- ⚠️ **Backend**: RESTful API, Spring Data JPA, layered architecture, 2+ custom classes, exception handling, 50%+ test coverage, authentication/authorization
- ⚠️ **Frontend**: React SPA, 2+ routes, HTTP requests, 5+ components, two-way binding
- ⚠️ **Git**: Branching strategy, README.md, Description file with user stories, wireframes, ERD, API docs

### Current Status
- ✅ Database schema defined (7 tables with 3 Many-to-Many relationships)
- ✅ Entity models created (Listener, Song, Artist, Album, Playlist, History)
- ✅ DTOs created
- ✅ Repositories created
- ⚠️ Controllers exist but return mock data
- ❌ Service layer not implemented
- ❌ Spotify API integration not implemented
- ❌ Authentication/Authorization not implemented
- ❌ Exception handling not implemented
- ❌ Tests not written
- ❌ Frontend not implemented
- ❌ Documentation incomplete

---

## Phase 1: Database Setup & Configuration

### Step 1.1: Verify Database Schema
**Location**: `database/init.sql`

**Tasks**:
1. ✅ Verify all tables are in 3NF (already done)
2. ✅ Confirm 5+ tables exist (7 tables: artist, song, listeners, playlists, history, plus junction tables)
3. ✅ Verify Many-to-Many relationships:
   - `artists_songs` (Artist ↔ Song)
   - `playlists_songs` (Playlist ↔ Song)
   - `albums_songs` (Album ↔ Song)

**Action Items**:
- [ ] Add missing `album` table to `init.sql` (currently only in model, not in SQL)
- [ ] Verify foreign key constraints
- [ ] Test database initialization

### Step 1.2: Update Database Schema
Add the missing `album` table to `database/init.sql`:

```sql
-- 06_album.sql
CREATE TABLE album (
    album_id     VARCHAR(64) PRIMARY KEY,
    title        TEXT        NOT NULL,
    release_year INTEGER,
    href         TEXT,
    artist_id    VARCHAR(64) REFERENCES artist(artist_id)
);

-- Junction table for albums and songs
CREATE TABLE albums_songs (
    album_id  VARCHAR(64) NOT NULL REFERENCES album(album_id),
    song_id   VARCHAR(64) NOT NULL REFERENCES song(song_id),
    PRIMARY KEY (album_id, song_id)
);
```

### Step 1.3: Configure Spring Data JPA
**Location**: `backend/src/main/resources/application.properties`

**Tasks**:
1. Add PostgreSQL connection properties
2. Configure JPA/Hibernate settings
3. Set up database initialization strategy

**Configuration**:
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/spotifydb
spring.datasource.username=spotify_user
spring.datasource.password=spotify_pass
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Application Configuration
spring.application.name=Feedback-fm
server.port=8080

# CORS Configuration (for development)
spring.web.cors.allowed-origins=http://localhost:3000
```

### Step 1.4: Add PostgreSQL Dependency
**Location**: `backend/pom.xml`

Add PostgreSQL driver dependency:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## Phase 2: Backend Foundation

### Step 2.1: Implement Service Layer
**Goal**: Separate business logic from controllers

**Create Service Interfaces**:
1. `ListenerService.java`
2. `SongService.java`
3. `ArtistService.java`
4. `AlbumService.java`
5. `PlaylistService.java`
6. `HistoryService.java`
7. `SpotifyService.java` (for Spotify API calls)

**Service Structure Example** (`ListenerService.java`):
```java
package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.ListenerDTO;
import java.util.List;
import java.util.Optional;

public interface ListenerService {
    List<ListenerDTO> getAllListeners();
    Optional<ListenerDTO> getListenerById(String listenerId);
    ListenerDTO createListener(ListenerDTO listenerDTO);
    ListenerDTO updateListener(String listenerId, ListenerDTO listenerDTO);
    void deleteListener(String listenerId);
    Optional<ListenerDTO> getListenerByEmail(String email);
}
```

**Service Implementation Example** (`ListenerServiceImpl.java`):
```java
package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.ListenerDTO;
import com.feedback.fm.feedbackfm.model.Listener;
import com.feedback.fm.feedbackfm.repository.ListenerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ListenerServiceImpl implements ListenerService {
    
    private final ListenerRepository listenerRepository;
    
    @Autowired
    public ListenerServiceImpl(ListenerRepository listenerRepository) {
        this.listenerRepository = listenerRepository;
    }
    
    @Override
    public List<ListenerDTO> getAllListeners() {
        return listenerRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public Optional<ListenerDTO> getListenerById(String listenerId) {
        return listenerRepository.findById(listenerId)
            .map(this::convertToDTO);
    }
    
    // Implement other methods...
    
    private ListenerDTO convertToDTO(Listener listener) {
        // Conversion logic
    }
    
    private Listener convertToEntity(ListenerDTO dto) {
        // Conversion logic
    }
}
```

### Step 2.2: Update Controllers to Use Services
**Location**: `backend/src/main/java/com/feedback/fm/feedbackfm/controller/`

**Tasks**:
1. Inject service dependencies
2. Replace mock data with service calls
3. Add proper HTTP status codes
4. Handle service responses

**Example** (`UserController.java` update):
```java
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    
    private final ListenerService listenerService;
    
    @Autowired
    public UserController(ListenerService listenerService) {
        this.listenerService = listenerService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ListenerDTO> getUserProfile(@PathVariable String id) {
        return listenerService.getListenerById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
```

### Step 2.3: Create Custom Classes
**Requirement**: At least 2 custom classes

**Suggested Custom Classes**:
1. **`SpotifyApiClient.java`** - Custom HTTP client for Spotify API
   - Location: `backend/src/main/java/com/feedback/fm/feedbackfm/service/spotify/`
   - Purpose: Encapsulate Spotify API communication logic

2. **`StatisticsCalculator.java`** - Calculate user statistics
   - Location: `backend/src/main/java/com/feedback/fm/feedbackfm/util/`
   - Purpose: Business logic for calculating top artists, songs, listening time, etc.

**Example** (`StatisticsCalculator.java`):
```java
package com.feedback.fm.feedbackfm.util;

import com.feedback.fm.feedbackfm.model.History;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsCalculator {
    
    public Map<String, Long> calculateTopArtists(List<History> history) {
        return history.stream()
            .map(h -> h.getSong().getArtists())
            .flatMap(Set::stream)
            .collect(Collectors.groupingBy(
                artist -> artist.getName(),
                Collectors.counting()
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }
    
    public long calculateTotalListeningTime(List<History> history) {
        return history.stream()
            .mapToLong(h -> h.getSong().getDurationMs())
            .sum();
    }
    
    // Add more calculation methods...
}
```

---

## Phase 3: Spotify API Integration

### Step 3.1: Set Up Spotify Developer Account
**Tasks**:
1. Go to [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
2. Create a new app
3. Get Client ID and Client Secret
4. Set redirect URI: `http://localhost:3000/callback` (for frontend)
5. Set backend redirect URI: `http://localhost:8080/api/auth/callback`

### Step 3.2: Add Spotify Dependencies
**Location**: `backend/pom.xml`

Add HTTP client dependency:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

Or use RestTemplate (already included in Spring Web):
```xml
<!-- Already included via spring-boot-starter-webmvc -->
```

### Step 3.3: Create Spotify Configuration
**Location**: `backend/src/main/resources/application.properties`

Add Spotify credentials:
```properties
# Spotify API Configuration
spotify.client.id=YOUR_CLIENT_ID
spotify.client.secret=YOUR_CLIENT_SECRET
spotify.redirect.uri=http://localhost:8080/api/auth/callback
spotify.api.base.url=https://api.spotify.com/v1
spotify.auth.url=https://accounts.spotify.com
```

**⚠️ Security Note**: Use environment variables or Spring profiles for production:
```properties
spotify.client.id=${SPOTIFY_CLIENT_ID}
spotify.client.secret=${SPOTIFY_CLIENT_SECRET}
```

### Step 3.4: Implement Spotify Service
**Location**: `backend/src/main/java/com/feedback/fm/feedbackfm/service/spotify/`

**Create Files**:
1. `SpotifyAuthService.java` - Handle OAuth flow
2. `SpotifyApiService.java` - Make API calls to Spotify

**Example** (`SpotifyAuthService.java`):
```java
package com.feedback.fm.feedbackfm.service.spotify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class SpotifyAuthService {
    
    @Value("${spotify.client.id}")
    private String clientId;
    
    @Value("${spotify.client.secret}")
    private String clientSecret;
    
    @Value("${spotify.redirect.uri}")
    private String redirectUri;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public String getAuthorizationUrl() {
        String scope = "user-read-private user-read-email user-read-recently-played user-top-read user-read-currently-playing";
        return "https://accounts.spotify.com/authorize" +
            "?client_id=" + clientId +
            "&response_type=code" +
            "&redirect_uri=" + redirectUri +
            "&scope=" + scope;
    }
    
    public String exchangeCodeForToken(String code) {
        String url = "https://accounts.spotify.com/api/token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String auth = clientId + ":" + clientSecret;
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes()));
        
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        
        return (String) response.getBody().get("access_token");
    }
    
    public String refreshToken(String refreshToken) {
        // Implement token refresh logic
        return null;
    }
}
```

**Example** (`SpotifyApiService.java`):
```java
package com.feedback.fm.feedbackfm.service.spotify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SpotifyApiService {
    
    @Value("${spotify.api.base.url}")
    private String apiBaseUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public Map<String, Object> getCurrentUser(String accessToken) {
        String url = apiBaseUrl + "/me";
        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            url, HttpMethod.GET, entity, Map.class
        );
        return response.getBody();
    }
    
    public Map<String, Object> getCurrentlyPlaying(String accessToken) {
        String url = apiBaseUrl + "/me/player/currently-playing";
        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null; // No song currently playing
        }
    }
    
    public Map<String, Object> getTopArtists(String accessToken, String timeRange) {
        String url = apiBaseUrl + "/me/top/artists?time_range=" + timeRange + "&limit=50";
        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            url, HttpMethod.GET, entity, Map.class
        );
        return response.getBody();
    }
    
    public Map<String, Object> getTopTracks(String accessToken, String timeRange) {
        String url = apiBaseUrl + "/me/top/tracks?time_range=" + timeRange + "&limit=50";
        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            url, HttpMethod.GET, entity, Map.class
        );
        return response.getBody();
    }
    
    public Map<String, Object> getRecentlyPlayed(String accessToken, int limit) {
        String url = apiBaseUrl + "/me/player/recently-played?limit=" + limit;
        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            url, HttpMethod.GET, entity, Map.class
        );
        return response.getBody();
    }
    
    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
```

### Step 3.5: Update SpotifyAuthController
**Location**: `backend/src/main/java/com/feedback/fm/feedbackfm/controller/SpotifyAuthController.java`

```java
package com.feedback.fm.feedbackfm.controller;

import com.feedback.fm.feedbackfm.service.spotify.SpotifyAuthService;
import com.feedback.fm.feedbackfm.service.spotify.SpotifyApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class SpotifyAuthController {
    
    private final SpotifyAuthService authService;
    private final SpotifyApiService apiService;
    
    @Autowired
    public SpotifyAuthController(SpotifyAuthService authService, SpotifyApiService apiService) {
        this.authService = authService;
        this.apiService = apiService;
    }
    
    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> getAuthUrl() {
        String authUrl = authService.getAuthorizationUrl();
        return ResponseEntity.ok(Map.of("authUrl", authUrl));
    }
    
    @GetMapping("/callback")
    public ResponseEntity<Map<String, Object>> handleCallback(@RequestParam String code) {
        String accessToken = authService.exchangeCodeForToken(code);
        Map<String, Object> userProfile = apiService.getCurrentUser(accessToken);
        
        // Save user to database, create session, etc.
        
        return ResponseEntity.ok(Map.of(
            "accessToken", accessToken,
            "user", userProfile
        ));
    }
}
```

---

## Phase 4: Service Layer Implementation

### Step 4.1: Implement Data Synchronization
**Goal**: Sync Spotify data to local database

**Create** `SpotifySyncService.java`:
```java
package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.model.*;
import com.feedback.fm.feedbackfm.repository.*;
import com.feedback.fm.feedbackfm.service.spotify.SpotifyApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.List;

@Service
@Transactional
public class SpotifySyncService {
    
    private final SpotifyApiService spotifyApiService;
    private final ListenerRepository listenerRepository;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final HistoryRepository historyRepository;
    
    @Autowired
    public SpotifySyncService(/* inject dependencies */) {
        // Initialize
    }
    
    public void syncUserProfile(String accessToken) {
        Map<String, Object> spotifyUser = spotifyApiService.getCurrentUser(accessToken);
        // Convert and save to Listener entity
    }
    
    public void syncRecentlyPlayed(String accessToken, String listenerId) {
        Map<String, Object> recentlyPlayed = spotifyApiService.getRecentlyPlayed(accessToken, 50);
        // Parse and save to History entity
    }
    
    public void syncTopArtists(String accessToken, String listenerId) {
        // Sync top artists data
    }
    
    // Implement other sync methods...
}
```

### Step 4.2: Implement Business Logic Services
Complete implementation of all service interfaces with proper business logic.

---

## Phase 5: Authentication & Authorization

### Step 5.1: Choose Authentication Strategy
**Options**:
1. **JWT (Recommended)**: Stateless, scalable
2. **Session-based**: Simpler, stateful
3. **OAuth2 with Spring Security**: Most robust

### Step 5.2: Implement JWT Authentication
**Add Dependencies** (`pom.xml`):
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

**Create JWT Utility** (`JwtUtil.java`):
```java
package com.feedback.fm.feedbackfm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public String generateToken(String listenerId) {
        return Jwts.builder()
            .subject(listenerId)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact();
    }
    
    public String extractListenerId(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Boolean validateToken(String token, String listenerId) {
        final String extractedId = extractListenerId(token);
        return (extractedId.equals(listenerId) && !isTokenExpired(token));
    }
    
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
```

**Create JWT Filter** (`JwtAuthenticationFilter.java`):
```java
package com.feedback.fm.feedbackfm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain chain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        
        String listenerId = null;
        String jwt = null;
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                listenerId = jwtUtil.extractListenerId(jwt);
            } catch (Exception e) {
                // Invalid token
            }
        }
        
        if (listenerId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt, listenerId)) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(listenerId, null, new ArrayList<>());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request, response);
    }
}
```

**Add Spring Security** (`pom.xml`):
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

**Create Security Config** (`SecurityConfig.java`):
```java
package com.feedback.fm.feedbackfm.config;

import com.feedback.fm.feedbackfm.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

**Add JWT Properties** (`application.properties`):
```properties
jwt.secret=your-secret-key-minimum-256-bits-long-for-hs256-algorithm
jwt.expiration=86400000
```

---

## Phase 6: Exception Handling

### Step 6.1: Create Custom Exceptions
**Location**: `backend/src/main/java/com/feedback/fm/feedbackfm/exception/`

**Create Files**:
1. `ResourceNotFoundException.java`
2. `InvalidRequestException.java`
3. `SpotifyApiException.java`
4. `AuthenticationException.java`

**Example** (`ResourceNotFoundException.java`):
```java
package com.feedback.fm.feedbackfm.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, String id) {
        super(resource + " not found with id: " + id);
    }
}
```

### Step 6.2: Create Global Exception Handler
**Create** `GlobalExceptionHandler.java`:
```java
package com.feedback.fm.feedbackfm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Resource Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequest(
            InvalidRequestException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Invalid Request");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(SpotifyApiException.class)
    public ResponseEntity<Map<String, Object>> handleSpotifyApiException(
            SpotifyApiException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("error", "Spotify API Error");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred");
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        // Log the full exception for debugging
        ex.printStackTrace();
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

### Step 6.3: Update Services to Throw Exceptions
Update all service methods to throw appropriate exceptions instead of returning null or empty.

---

## Phase 7: Testing

### Step 7.1: Add Testing Dependencies
**Location**: `backend/pom.xml`

Verify these are present:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

### Step 7.2: Write Service Layer Tests
**Goal**: Achieve 50%+ line coverage using Mockito

**Create Test Structure**:
- `ListenerServiceTest.java`
- `SongServiceTest.java`
- `ArtistServiceTest.java`
- `PlaylistServiceTest.java`
- `HistoryServiceTest.java`

**Example** (`ListenerServiceTest.java`):
```java
package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.ListenerDTO;
import com.feedback.fm.feedbackfm.model.Listener;
import com.feedback.fm.feedbackfm.repository.ListenerRepository;
import com.feedback.fm.feedbackfm.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListenerServiceTest {
    
    @Mock
    private ListenerRepository listenerRepository;
    
    @InjectMocks
    private ListenerServiceImpl listenerService;
    
    private Listener testListener;
    private ListenerDTO testListenerDTO;
    
    @BeforeEach
    void setUp() {
        testListener = new Listener("listener123", "Test User", "test@example.com", "US", "https://spotify.com/user");
        testListenerDTO = new ListenerDTO("listener123", "Test User", "test@example.com", "US", "https://spotify.com/user");
    }
    
    @Test
    void getListenerById_WhenExists_ReturnsListenerDTO() {
        // Arrange
        when(listenerRepository.findById("listener123")).thenReturn(Optional.of(testListener));
        
        // Act
        Optional<ListenerDTO> result = listenerService.getListenerById("listener123");
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("listener123", result.get().getListenerId());
        verify(listenerRepository, times(1)).findById("listener123");
    }
    
    @Test
    void getListenerById_WhenNotExists_ReturnsEmpty() {
        // Arrange
        when(listenerRepository.findById("nonexistent")).thenReturn(Optional.empty());
        
        // Act
        Optional<ListenerDTO> result = listenerService.getListenerById("nonexistent");
        
        // Assert
        assertFalse(result.isPresent());
        verify(listenerRepository, times(1)).findById("nonexistent");
    }
    
    @Test
    void createListener_WhenValid_ReturnsListenerDTO() {
        // Arrange
        when(listenerRepository.save(any(Listener.class))).thenReturn(testListener);
        
        // Act
        ListenerDTO result = listenerService.createListener(testListenerDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals("listener123", result.getListenerId());
        verify(listenerRepository, times(1)).save(any(Listener.class));
    }
    
    // Add more test methods...
}
```

### Step 7.3: Write Controller Tests
**Example** (`UserControllerTest.java`):
```java
package com.feedback.fm.feedbackfm.controller;

import com.feedback.fm.feedbackfm.dtos.ListenerDTO;
import com.feedback.fm.feedbackfm.service.ListenerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ListenerService listenerService;
    
    @Test
    void getUserProfile_WhenExists_Returns200() throws Exception {
        // Arrange
        ListenerDTO dto = new ListenerDTO("listener123", "Test User", "test@example.com", "US", "https://spotify.com/user");
        when(listenerService.getListenerById("listener123")).thenReturn(Optional.of(dto));
        
        // Act & Assert
        mockMvc.perform(get("/api/users/listener123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.listenerId").value("listener123"));
    }
    
    @Test
    void getUserProfile_WhenNotExists_Returns404() throws Exception {
        // Arrange
        when(listenerService.getListenerById(anyString())).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(get("/api/users/nonexistent"))
            .andExpect(status().isNotFound());
    }
}
```

### Step 7.4: Generate Coverage Report
**Run tests with coverage**:
```bash
mvn clean test jacoco:report
```

**View coverage**: Open `target/site/jacoco/index.html`

**Add JaCoCo Plugin** (`pom.xml`):
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

## Phase 8: Frontend Development

### Step 8.1: Set Up React Project
**Location**: `frontend/`

**Install Dependencies**:
```bash
cd frontend
npm install react react-dom react-router-dom axios
npm install -D @types/react @types/react-dom @vitejs/plugin-react
```

**Update** `package.json`:
```json
{
  "name": "feedback-fm-frontend",
  "version": "0.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.20.0",
    "axios": "^1.6.0"
  },
  "devDependencies": {
    "@types/react": "^18.2.0",
    "@types/react-dom": "^18.2.0",
    "@vitejs/plugin-react": "^4.2.0",
    "typescript": "~5.9.3",
    "vite": "^7.2.4"
  }
}
```

**Create** `vite.config.ts`:
```typescript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

### Step 8.2: Create Component Structure
**Required**: At least 5 components

**Suggested Components**:
1. `App.tsx` - Main app component with routing
2. `Dashboard.tsx` - User dashboard
3. `TopArtists.tsx` - Display top artists
4. `TopSongs.tsx` - Display top songs
5. `CurrentlyPlaying.tsx` - Show currently playing track
6. `ListeningHistory.tsx` - Show listening history
7. `PlaylistList.tsx` - List user playlists
8. `Login.tsx` - Login/Spotify auth
9. `Navbar.tsx` - Navigation component
10. `StatsCard.tsx` - Reusable stats card component

**Create Directory Structure**:
```
frontend/src/
├── components/
│   ├── Dashboard.tsx
│   ├── TopArtists.tsx
│   ├── TopSongs.tsx
│   ├── CurrentlyPlaying.tsx
│   ├── ListeningHistory.tsx
│   ├── PlaylistList.tsx
│   ├── Login.tsx
│   ├── Navbar.tsx
│   └── StatsCard.tsx
├── services/
│   └── api.ts
├── App.tsx
├── main.tsx
└── style.css
```

### Step 8.3: Implement Routing
**Requirement**: At least 2 separate pages/views

**Update** `App.tsx`:
```typescript
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import TopArtists from './components/TopArtists';
import TopSongs from './components/TopSongs';
import CurrentlyPlaying from './components/CurrentlyPlaying';
import ListeningHistory from './components/ListeningHistory';
import PlaylistList from './components/PlaylistList';

function App() {
  return (
    <Router>
      <div className="App">
        <Navbar />
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/top-artists" element={<TopArtists />} />
          <Route path="/top-songs" element={<TopSongs />} />
          <Route path="/currently-playing" element={<CurrentlyPlaying />} />
          <Route path="/history" element={<ListeningHistory />} />
          <Route path="/playlists" element={<PlaylistList />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
```

### Step 8.4: Create API Service
**Create** `services/api.ts`:
```typescript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const authAPI = {
  getAuthUrl: () => api.get('/auth/login'),
  handleCallback: (code: string) => api.get(`/auth/callback?code=${code}`),
};

export const userAPI = {
  getProfile: (id: string) => api.get(`/users/${id}`),
  getDashboard: (id: string) => api.get(`/users/${id}/dashboard`),
  getStats: (id: string) => api.get(`/users/${id}/stats`),
};

export const artistsAPI = {
  getTopArtists: (timeRange: string = 'medium_term') => 
    api.get(`/artists/top?time_range=${timeRange}`),
};

export const songsAPI = {
  getTopSongs: (timeRange: string = 'medium_term') => 
    api.get(`/songs/top?time_range=${timeRange}`),
  getCurrentlyPlaying: () => api.get('/songs/currently-playing'),
};

export const historyAPI = {
  getHistory: (limit: number = 50) => api.get(`/history?limit=${limit}`),
};

export const playlistsAPI = {
  getAll: () => api.get('/playlists'),
  getById: (id: string) => api.get(`/playlists/${id}`),
};

export default api;
```

### Step 8.5: Implement Two-Way Binding
**Example** (`Login.tsx`):
```typescript
import { useState } from 'react';
import { authAPI } from '../services/api';

function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    // Handle login
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        placeholder="Username"
      />
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Password"
      />
      <button type="submit">Login</button>
    </form>
  );
}

export default Login;
```

### Step 8.6: Implement HTTP Requests
**Example** (`Dashboard.tsx`):
```typescript
import { useEffect, useState } from 'react';
import { userAPI } from '../services/api';

function Dashboard() {
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const userId = localStorage.getItem('userId');
        const response = await userAPI.getDashboard(userId!);
        setDashboardData(response.data);
      } catch (error) {
        console.error('Error fetching dashboard:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboard();
  }, []);

  if (loading) return <div>Loading...</div>;

  return (
    <div>
      <h1>Dashboard</h1>
      {/* Render dashboard data */}
    </div>
  );
}

export default Dashboard;
```

---

## Phase 9: Documentation

### Step 9.1: Create Description File
**Location**: `documentation/DESCRIPTION.md`

**Include**:
1. **App Description**: What the app does, its purpose, features
2. **User Stories**: Format: "As a ____, I want to ____, so that I can ____"
3. **Wireframes**: Frontend UI mockups
4. **ERD**: Entity Relationship Diagram
5. **API Documentation**: Endpoint documentation

**Example User Stories**:
```
As a Spotify user, I want to view my top artists, so that I can see my music preferences.
As a Spotify user, I want to see my currently playing track, so that I can share what I'm listening to.
As a Spotify user, I want to view my listening history, so that I can track my music consumption.
As a Spotify user, I want to see my top songs, so that I can discover my favorite tracks.
As a Spotify user, I want to view my playlists, so that I can manage my music collections.
```

### Step 9.2: Create API Documentation
**Location**: `documentation/API_DOCUMENTATION.md`

**Format**:
```markdown
# API Documentation

## Authentication

### GET /api/auth/login
Returns Spotify authorization URL.

**Response:**
```json
{
  "authUrl": "https://accounts.spotify.com/authorize?..."
}
```

### GET /api/auth/callback
Handles OAuth callback from Spotify.

**Query Parameters:**
- `code` (string, required): Authorization code from Spotify

**Response:**
```json
{
  "accessToken": "BQD...",
  "user": { ... }
}
```

## Users

### GET /api/users/{id}
Get user profile by ID.

**Path Parameters:**
- `id` (string, required): User ID

**Response:**
```json
{
  "listenerId": "user123",
  "displayName": "John Doe",
  "email": "john@example.com",
  "country": "US",
  "href": "https://spotify.com/user/user123"
}
```

[... continue for all endpoints ...]
```

### Step 9.3: Create ERD
**Tools**: Draw.io, Lucidchart, or database visualization tool

**Include**:
- All tables
- Relationships (One-to-Many, Many-to-Many)
- Primary keys
- Foreign keys

### Step 9.4: Create Wireframes
**Tools**: Figma, Balsamiq, or hand-drawn sketches

**Include wireframes for**:
- Login page
- Dashboard
- Top Artists page
- Top Songs page
- Currently Playing page
- Listening History page
- Playlists page

### Step 9.5: Update README.md
**Location**: `README.md` (root)

**Include**:
- Project description
- Technologies used
- Prerequisites
- Installation instructions
- How to run the application
- Database setup
- API endpoints overview
- Testing instructions
- Contributing guidelines

---

## Phase 10: Git Workflow & Best Practices

### Step 10.1: Set Up Branching Strategy
**Strategy**: Feature branch workflow

**Branches**:
- `main` - Production-ready code (protected)
- `develop` - Integration branch
- `feature/*` - Feature branches (e.g., `feature/spotify-auth`, `feature/user-dashboard`)
- `bugfix/*` - Bug fix branches
- `hotfix/*` - Critical production fixes

**Commands**:
```bash
# Create feature branch
git checkout -b feature/spotify-integration

# Work on feature
git add .
git commit -m "feat: implement Spotify OAuth flow"

# Push to remote
git push origin feature/spotify-integration

# Create pull request to merge into develop/main
```

### Step 10.2: Commit Message Convention
**Format**: `type(scope): description`

**Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting
- `refactor`: Code refactoring
- `test`: Adding tests
- `chore`: Maintenance tasks

**Examples**:
```
feat(auth): implement JWT authentication
fix(api): handle null pointer in user service
docs(readme): update installation instructions
test(service): add unit tests for ListenerService
```

### Step 10.3: Protect Main Branch
**Settings** (GitHub/GitLab):
1. Require pull request reviews
2. Require status checks to pass
3. Require branches to be up to date
4. Do not allow force pushes
5. Do not allow deletions

### Step 10.4: Create .gitignore
**Ensure** `.gitignore` includes:
```
# Java
*.class
*.jar
*.war
target/
.mvn/

# Node
node_modules/
dist/
*.log

# IDE
.idea/
.vscode/
*.iml

# Environment
.env
application-local.properties

# OS
.DS_Store
Thumbs.db
```

---

## Development Timeline

### Week 1: Foundation
- [ ] Complete Phase 1 (Database)
- [ ] Complete Phase 2 (Backend Foundation)
- [ ] Set up Git workflow

### Week 2: Integration
- [ ] Complete Phase 3 (Spotify API)
- [ ] Complete Phase 4 (Service Layer)
- [ ] Start Phase 5 (Authentication)

### Week 3: Security & Testing
- [ ] Complete Phase 5 (Authentication)
- [ ] Complete Phase 6 (Exception Handling)
- [ ] Complete Phase 7 (Testing) - Aim for 50%+ coverage

### Week 4: Frontend
- [ ] Complete Phase 8 (Frontend Development)
- [ ] Integration testing
- [ ] UI/UX polish

### Week 5: Documentation & Polish
- [ ] Complete Phase 9 (Documentation)
- [ ] Complete Phase 10 (Git workflow)
- [ ] Final testing
- [ ] Presentation preparation

---

## Common Issues & Solutions

### Issue: Database Connection Failed
**Solution**: 
- Verify Docker container is running: `docker ps`
- Check `application.properties` credentials
- Verify PostgreSQL is accessible on port 5432

### Issue: CORS Errors
**Solution**:
- Add `@CrossOrigin` to controllers
- Configure CORS in `SecurityConfig`
- Verify frontend URL matches allowed origins

### Issue: Spotify API Rate Limiting
**Solution**:
- Implement request caching
- Add rate limiting logic
- Use batch requests where possible

### Issue: JWT Token Expired
**Solution**:
- Implement token refresh mechanism
- Store refresh token securely
- Handle 401 responses in frontend

---

## Resources

- [Spotify Web API Documentation](https://developer.spotify.com/documentation/web-api)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [JWT.io](https://jwt.io/) - JWT debugging tool

---

## History Entity Structure: Many-to-Many or Event Log?

### Question: Should History be a Many-to-Many Relationship?

**Answer: No, the current structure is correct and will work perfectly for the frontend.**

### Current Structure Analysis

The `History` entity is currently structured as:
- **History** (Entity/Table)
  - `history_id` (Primary Key)
  - `listener_id` (Foreign Key → Listener)
  - `song_id` (Foreign Key → Song)
  - `played_at` (Timestamp)

This is a **Many-to-One relationship** (not Many-to-Many):
- Many History records can belong to one Listener
- Many History records can reference one Song
- Each History record represents **one listening event** at a specific time

### Why This Structure Works for Frontend Display

✅ **Perfect for displaying listening history** because:

1. **Chronological Order**: The `played_at` timestamp allows you to sort and display songs in the order they were played
   ```sql
   SELECT * FROM history 
   WHERE listener_id = 'user123' 
   ORDER BY played_at DESC 
   LIMIT 50;
   ```

2. **Duplicate Songs Allowed**: The same song can appear multiple times if the user listened to it multiple times (which is realistic!)
   - Example: User listens to "Bohemian Rhapsody" 3 times → 3 History records

3. **Rich Querying Capabilities**: You can easily query:
   - Recent songs: `ORDER BY played_at DESC`
   - Most played songs: `GROUP BY song_id ORDER BY COUNT(*) DESC`
   - Songs played today: `WHERE played_at >= CURRENT_DATE`
   - Listening streaks: Analyze consecutive days with plays

4. **Event-Based Tracking**: This is an **audit log/event log** pattern, which is ideal for tracking user activity over time

### What a Many-to-Many Would Look Like (NOT Recommended)

If History were a Many-to-Many relationship, it would be:
- A junction table: `listeners_songs` with just `listener_id` and `song_id`
- **Problems**:
  - ❌ No timestamp (can't show when songs were played)
  - ❌ No duplicate tracking (can't show if a song was played multiple times)
  - ❌ No chronological ordering
  - ❌ Can't calculate "recently played" - only "ever played"

### Frontend Implementation Example

With the current structure, your frontend can easily display:

**Recent Listening History**:
```typescript
// Backend endpoint
GET /api/history?listenerId=user123&limit=50

// Returns:
[
  {
    "historyId": 1,
    "song": {
      "songId": "song123",
      "name": "Bohemian Rhapsody",
      "artists": [...],
      "durationMs": 355000
    },
    "playedAt": "2024-01-15T14:30:00"
  },
  {
    "historyId": 2,
    "song": {
      "songId": "song456",
      "name": "Stairway to Heaven",
      ...
    },
    "playedAt": "2024-01-15T14:25:00"
  }
  // ... more entries
]
```

**Frontend Component**:
```typescript
function ListeningHistory() {
  const [history, setHistory] = useState([]);
  
  useEffect(() => {
    const fetchHistory = async () => {
      const response = await historyAPI.getHistory(50);
      setHistory(response.data);
    };
    fetchHistory();
  }, []);
  
  return (
    <div>
      <h2>Recently Played</h2>
      {history.map(item => (
        <div key={item.historyId}>
          <span>{item.song.name}</span>
          <span>{formatDate(item.playedAt)}</span>
        </div>
      ))}
    </div>
  );
}
```

### Database Query Examples

**Get recent songs for a listener**:
```java
@Query("SELECT h FROM History h WHERE h.listener.listenerId = :listenerId ORDER BY h.playedAt DESC")
List<History> findRecentByListenerId(@Param("listenerId") String listenerId, Pageable pageable);
```

**Get most played songs**:
```java
@Query("SELECT h.song, COUNT(h) as playCount FROM History h WHERE h.listener.listenerId = :listenerId GROUP BY h.song ORDER BY playCount DESC")
List<Object[]> findMostPlayedSongs(@Param("listenerId") String listenerId);
```

### Conclusion

✅ **Keep the current History structure** - it's perfect for your use case!

- The current `History` entity with `ManyToOne` relationships to `Listener` and `Song` is the correct design
- It functions as an **event log** that tracks listening events over time
- This structure provides all the data needed for frontend display:
  - Chronological ordering
  - Recent songs
  - Play frequency
  - Time-based filtering
- No changes needed - proceed with frontend implementation using this structure

### Summary: Many-to-Many Relationships in Your Project

Your project already has **3 Many-to-Many relationships** (meeting the requirement):
1. ✅ **Artist ↔ Song** (`artists_songs` junction table)
2. ✅ **Playlist ↔ Song** (`playlists_songs` junction table)
3. ✅ **Album ↔ Song** (`albums_songs` junction table)

The `History` entity is **not** a Many-to-Many - it's an event log, which is the correct design pattern for tracking user activity over time.

---

## Album-Song Relationship: Many-to-Many or Many-to-One?

### Question: Does the Album-Song Many-to-Many Make Sense?

**Answer: For a Spotify statistics application, a Many-to-One relationship (Song → Album) is more appropriate and simpler.**

### Current Structure Analysis

Currently, you have:
- **Album ↔ Song**: Many-to-Many relationship (`albums_songs` junction table)
- **Album → Artist**: Many-to-One relationship (one album belongs to one artist)

### Why Many-to-Many May Not Be Ideal

#### 1. **Spotify API Data Model**
- Spotify's Web API returns **one primary album** per track
- When you fetch a track, it has a single `album` object, not an array
- Example Spotify API response:
  ```json
  {
    "id": "song123",
    "name": "Bohemian Rhapsody",
    "album": {
      "id": "album456",
      "name": "A Night at the Opera",
      "artists": [...]
    }
  }
  ```

#### 2. **Use Case for Your Application**
For displaying statistics like:
- "Top Albums" - You want to show albums the user has listened to most
- "Recent Albums" - Show albums from recently played songs
- "Album Details" - Show all songs in an album

A **Many-to-One** relationship (Song → Album) is sufficient because:
- Each song has one primary album (from Spotify)
- You can easily query: "Get all songs from this album"
- You can easily query: "Get the album for this song"
- Simpler data model = simpler queries = better performance

#### 3. **Real-World Edge Cases**
While technically a song *can* appear on multiple albums (singles, compilations, deluxe editions), for a Spotify stats app:
- Spotify's API doesn't provide this multi-album information easily
- Users typically think of a song as belonging to one album
- The complexity of tracking multiple albums per song isn't worth it for this use case

### Recommendation: Change to Many-to-One

**Change the relationship from Many-to-Many to Many-to-One:**

#### Updated Song Model:
```java
@Entity
@Table(name = "song")
public class Song {
    // ... existing fields ...
    
    // Change from ManyToMany to ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;  // Changed from Set<Album> albums
    
    // ... rest of the class ...
}
```

#### Updated Album Model:
```java
@Entity
@Table(name = "album")
public class Album {
    // ... existing fields ...
    
    // Change from ManyToMany to OneToMany
    @OneToMany(mappedBy = "album")
    private Set<Song> songs = new HashSet<>();  // Keep as Set, but now OneToMany
    
    // ... rest of the class ...
}
```

#### Updated Database Schema:
```sql
-- Update song table to include album_id foreign key
ALTER TABLE song ADD COLUMN album_id VARCHAR(64) REFERENCES album(album_id);

-- Remove the junction table (after migrating data if needed)
DROP TABLE albums_songs;
```

### Benefits of Many-to-One

✅ **Simpler Data Model**
- Easier to understand and maintain
- Fewer tables to manage
- Simpler queries

✅ **Better Performance**
- No junction table joins needed
- Direct foreign key relationship
- Faster queries for "get album for song" or "get songs for album"

✅ **Matches Spotify API**
- Aligns with how Spotify structures its data
- Easier to sync data from Spotify API
- Less data transformation needed

✅ **Still Meets Requirements**
- You already have **2 Many-to-Many relationships**:
  1. Artist ↔ Song (artists_songs)
  2. Playlist ↔ Song (playlists_songs)
- This meets the "at least 1 Many-to-Many" requirement
- You don't need a third one

### When Many-to-Many Would Make Sense

A Many-to-Many relationship would be appropriate if you needed to:
- Track which songs appear on compilation albums
- Show "This song also appears on:" functionality
- Handle complex music catalog scenarios (like a music library app)
- Support user-created album collections where songs can be in multiple custom albums

**For a Spotify statistics app, these features are not necessary.**

### Migration Strategy

If you decide to change from Many-to-Many to Many-to-One:

1. **Data Migration** (if you have existing data):
   ```sql
   -- If a song appears in multiple albums, pick the first one
   UPDATE song s
   SET album_id = (
       SELECT album_id 
       FROM albums_songs 
       WHERE song_id = s.song_id 
       LIMIT 1
   );
   ```

2. **Update Models**: Change annotations as shown above

3. **Update Services**: Simplify queries (no more junction table joins)

4. **Update DTOs**: Change `List<String> songIds` in AlbumDTO to just track songs via the OneToMany relationship

5. **Update Database Schema**: Add `album_id` to `song` table, drop `albums_songs` table

### Alternative: Keep It But Simplify Usage

If you want to keep the Many-to-Many for flexibility but simplify usage:

- In your service layer, when syncing from Spotify, only add the song to **one album** (the primary album)
- Treat it as a Many-to-One in practice, but keep the structure for future flexibility
- Document that you're only using one album per song currently

**However, this adds unnecessary complexity without benefit.**

### Final Recommendation

**✅ Change to Many-to-One (Song → Album)**

**Reasons:**
1. Simpler and more maintainable
2. Matches Spotify's data model
3. Better performance
4. Still meets project requirements (you have 2 other Many-to-Many relationships)
5. Easier to implement and test

**Action Items:**
- [ ] Update `Song` model: Change `Set<Album> albums` to `Album album` with `@ManyToOne`
- [ ] Update `Album` model: Change `@ManyToMany` to `@OneToMany` for songs
- [ ] Update database schema: Add `album_id` to `song` table
- [ ] Remove `albums_songs` junction table
- [ ] Update service layer to work with single album per song
- [ ] Update DTOs if needed

---

## Next Steps

1. Review this guide with your team
2. Assign tasks based on phases
3. Set up development environment
4. Create initial feature branches
5. Begin Phase 1 implementation

**Good luck with your project! 🎵**
