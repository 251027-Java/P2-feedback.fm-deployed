# Development Guide: feedback.fm

A comprehensive step-by-step guide for developing a full-stack Spotify statistics application similar to Last.fm.

## Table of Contents
1. [Project Overview](#project-overview)
2. [Next Steps - Priority Order](#next-steps---priority-order)
3. [Phase 5: Complete JWT Integration](#phase-5-complete-jwt-integration)
4. [Phase 6: Exception Handling](#phase-6-exception-handling)
5. [Phase 7: Testing](#phase-7-testing)
6. [Phase 8: Frontend Development](#phase-8-frontend-development)
7. [Phase 9: Documentation](#phase-9-documentation)
8. [Phase 10: Git Workflow & Best Practices](#phase-10-git-workflow--best-practices)

---

## Project Overview

### Requirements Checklist
- ✅ **Database**: PostgreSQL in Docker, 3NF, 5+ tables, 1+ Many-to-Many relationship
- ⚠️ **Backend**: RESTful API, Spring Data JPA, layered architecture, 2+ custom classes, exception handling, 50%+ test coverage, authentication/authorization
- ⚠️ **Frontend**: React SPA, 2+ routes, HTTP requests, 5+ components, two-way binding
- ⚠️ **Git**: Branching strategy, README.md, Description file with user stories, wireframes, ERD, API docs

### Current Status

#### ✅ Completed Phases
- ✅ **Phase 1: Database Setup & Configuration** - Database schema, entities, DTOs, repositories all implemented
- ✅ **Phase 2: Backend Foundation** - Service layer with interfaces, transaction management, validation implemented
- ✅ **Phase 3: Spotify API Integration** - OAuth flow, API services, authentication controller implemented
- ✅ **Phase 4: Service Layer Implementation** - SpotifySyncService for data synchronization implemented
- ✅ **Phase 5: JWT Infrastructure** - JWT utilities, filter, and security config created

#### 🚧 In Progress / Next Steps
- ⚠️ **Phase 5: JWT Integration** - Need to integrate JWT token generation in SpotifyAuthController callback
- ⚠️ **Phase 6: Exception Handling** - Custom exceptions and global handler needed
- ⚠️ **Phase 7: Testing** - Unit and integration tests needed (50%+ coverage, currently only 1 test file)
- ⚠️ **Phase 8: Frontend Development** - React application needed
- ⚠️ **Phase 9: Documentation** - API docs, ERD, wireframes needed
- ⚠️ **Phase 10: Git Workflow** - Branching strategy and best practices

---

## Next Steps - Priority Order

### 🎯 IMMEDIATE PRIORITY: Complete JWT Integration

**Status**: JWT infrastructure exists but is not integrated with OAuth callback.

**What's Done**:
- ✅ `JwtUtil.java` - Token generation and validation
- ✅ `JwtAuthenticationFilter.java` - Request filtering
- ✅ `SecurityConfig.java` - Security configuration
- ✅ JWT dependencies in `pom.xml`
- ✅ JWT properties in `application.properties`

**What's Missing**:
- ❌ `SpotifyAuthController.handleCallback()` does not generate JWT tokens
- ❌ Currently returns Spotify access token instead of JWT
- ❌ Frontend cannot authenticate with JWT

**Action Required**: Update `SpotifyAuthController` to generate and return JWT tokens after successful OAuth.

---

## Phase 5: Complete JWT Integration

### Step 5.1: Update SpotifyAuthController to Generate JWT

**Location**: `backend/src/main/java/com/feedback/fm/feedbackfm/controller/SpotifyAuthController.java`

**Current Issue**: The callback method returns Spotify access tokens directly. It should generate a JWT token for the authenticated user.

**Required Changes**:

1. **Inject JwtUtil**:
```java
@Autowired
private JwtUtil jwtUtil;
```

2. **Update `handleCallback()` method** to generate JWT after successful authentication:
```java
@GetMapping("/callback")
public ResponseEntity<Map<String, Object>> handleCallback(@RequestParam String code) {
    try {
        // Exchange code for access token
        Map<String, Object> tokenResponse = authService.exchangeCodeForToken(code);
        String accessToken = (String) tokenResponse.get("access_token");
        String refreshToken = (String) tokenResponse.get("refresh_token");
        
        if (accessToken == null) {
            return ResponseEntity.status(400).body(Map.of("error", "Failed to get access token"));
        }
        
        // Get user profile from Spotify
        Map<String, Object> userProfile = apiService.getCurrentUser(accessToken);
        
        // Create or update listener in database
        String spotifyId = (String) userProfile.get("id");
        String displayName = (String) userProfile.get("display_name");
        String email = (String) userProfile.get("email");
        String country = (String) userProfile.get("country");
        Map<String, Object> externalUrls = (Map<String, Object>) userProfile.get("external_urls");
        String href = externalUrls != null ? (String) externalUrls.get("spotify") : null;
        
        // Check if listener exists, create or update
        var existingListener = listenerService.getById(spotifyId);
        if (existingListener.isEmpty()) {
            listenerService.create(new ListenerDTO(
                spotifyId, displayName, email, country, href
            ));
        } else {
            listenerService.update(spotifyId, new ListenerDTO(
                spotifyId, displayName, email, country, href
            ));
        }
        
        // ✅ NEW: Generate JWT token for the authenticated user
        String jwtToken = jwtUtil.generateToken(spotifyId);
        
        // Return response with JWT token (not Spotify access token)
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtToken);  // JWT token for API authentication
        response.put("listenerId", spotifyId);
        response.put("user", userProfile);
        // Optionally include Spotify tokens if needed for API calls
        response.put("spotifyAccessToken", accessToken);
        if (refreshToken != null) {
            response.put("spotifyRefreshToken", refreshToken);
        }
        
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Authentication failed");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(500).body(errorResponse);
    }
}
```

3. **Test the Integration**:
   - Start the backend server
   - Call `/api/auth/login` to get authorization URL
   - Complete OAuth flow
   - Verify JWT token is returned in callback response
   - Test protected endpoints with JWT token in `Authorization: Bearer <token>` header

### Step 5.2: Verify Security Configuration

**Check**: Ensure `SecurityConfig.java` properly protects endpoints:

- ✅ `/api/auth/**` - Public (permitAll)
- ✅ `/api/public/**` - Public (permitAll)
- ✅ All other endpoints - Require authentication

**Test**: Try accessing a protected endpoint without JWT token - should return 401 Unauthorized.

---

## Phase 6: Exception Handling

**Goal**: Replace `ResponseStatusException` with custom exceptions and create a global exception handler for consistent error responses.

**Why This is Important**:
- Provides consistent error response format across all endpoints
- Better error messages for frontend
- Easier debugging and logging
- Required for project completion

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

**Example** (`InvalidRequestException.java`):
```java
package com.feedback.fm.feedbackfm.exception;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
```

**Example** (`SpotifyApiException.java`):
```java
package com.feedback.fm.feedbackfm.exception;

public class SpotifyApiException extends RuntimeException {
    public SpotifyApiException(String message) {
        super(message);
    }
    
    public SpotifyApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

**Example** (`AuthenticationException.java`):
```java
package com.feedback.fm.feedbackfm.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
```

### Step 6.2: Create Global Exception Handler

**Create** `GlobalExceptionHandler.java`:
**Location**: `backend/src/main/java/com/feedback/fm/feedbackfm/exception/GlobalExceptionHandler.java`

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
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Authentication Failed");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
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

### Step 6.3: Update Services to Use Custom Exceptions

**Replace** `ResponseStatusException` with custom exceptions in all service implementations.

**Example** (in `ListenerServiceImpl.java`):
```java
// Before:
throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Listener not found with id: " + id);

// After:
throw new ResourceNotFoundException("Listener", id);
```

**Files to Update**:
- `ListenerServiceImpl.java`
- `SongServiceImpl.java`
- `ArtistServiceImpl.java`
- `AlbumServiceImpl.java`
- `PlaylistServiceImpl.java`
- `HistoryServiceImpl.java`
- `SpotifyAuthService.java`
- `SpotifyApiService.java`

---

## Phase 7: Testing

**Goal**: Achieve 50%+ test coverage using Mockito for service layer and MockMvc for controllers.

**Current Status**: Only 1 test file exists (`AlbumServiceTest.java`). Need comprehensive test coverage.

**Why This is Important**:
- Ensures code quality and reliability
- Catches bugs before production
- Required for project completion (50%+ coverage)

### Step 7.1: Verify Testing Dependencies

**Location**: `backend/pom.xml`

**Verify these are present**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Step 7.2: Write Service Layer Tests

**Goal**: Achieve 50%+ line coverage using Mockito

**Create Test Files** (following the pattern of `AlbumServiceTest.java`):
- `ListenerServiceTest.java` ✅ (Priority 1)
- `SongServiceTest.java` ✅ (Priority 1)
- `ArtistServiceTest.java` ✅ (Priority 1)
- `PlaylistServiceTest.java` ✅ (Priority 2)
- `HistoryServiceTest.java` ✅ (Priority 2)
- `AlbumServiceTest.java` ✅ (Already exists, expand if needed)

**Test Coverage Requirements**:
- Test all CRUD operations (create, read, update, delete)
- Test validation logic
- Test error cases (not found, conflicts, invalid input)
- Test relationship management methods (if applicable)

**Example Structure** (for `ListenerServiceTest.java`):
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
    void getById_WhenExists_ReturnsListenerDTO() {
        when(listenerRepository.findById("listener123")).thenReturn(Optional.of(testListener));
        
        Optional<ListenerDTO> result = listenerService.getById("listener123");
        
        assertTrue(result.isPresent());
        assertEquals("listener123", result.get().listenerId());
        verify(listenerRepository, times(1)).findById("listener123");
    }
    
    @Test
    void getById_WhenNotExists_ReturnsEmpty() {
        when(listenerRepository.findById("nonexistent")).thenReturn(Optional.empty());
        
        Optional<ListenerDTO> result = listenerService.getById("nonexistent");
        
        assertFalse(result.isPresent());
    }
    
    @Test
    void create_WhenValid_ReturnsListenerDTO() {
        when(listenerRepository.existsById("listener123")).thenReturn(false);
        when(listenerRepository.save(any(Listener.class))).thenReturn(testListener);
        
        ListenerDTO result = listenerService.create(testListenerDTO);
        
        assertNotNull(result);
        assertEquals("listener123", result.listenerId());
        verify(listenerRepository, times(1)).save(any(Listener.class));
    }
    
    @Test
    void create_WhenIdExists_ThrowsException() {
        when(listenerRepository.existsById("listener123")).thenReturn(true);
        
        assertThrows(Exception.class, () -> listenerService.create(testListenerDTO));
    }
    
    // Add more test methods for update, delete, validation, etc.
}
```

### Step 7.3: Write Controller Tests

**Create Test Files**:
- `ListenerControllerTest.java`
- `SpotifyAuthControllerTest.java`
- `SongControllerTest.java`
- `ArtistControllerTest.java`

**Example** (`ListenerControllerTest.java`):
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

@WebMvcTest(ListenerController.class)
class ListenerControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ListenerService listenerService;
    
    @Test
    void getUserProfile_WhenExists_Returns200() throws Exception {
        ListenerDTO dto = new ListenerDTO("listener123", "Test User", "test@example.com", "US", "https://spotify.com/user");
        when(listenerService.getById("listener123")).thenReturn(Optional.of(dto));
        
        mockMvc.perform(get("/api/users/listener123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.listenerId").value("listener123"));
    }
    
    @Test
    void getUserProfile_WhenNotExists_Returns404() throws Exception {
        when(listenerService.getById(anyString())).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/users/nonexistent"))
            .andExpect(status().isNotFound());
    }
}
```

### Step 7.4: Add JaCoCo Plugin for Coverage Reports

**Add to** `pom.xml`:
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

**Generate Coverage Report**:
```bash
mvn clean test jacoco:report
```

**View Coverage**: Open `target/site/jacoco/index.html` in a browser.

**Target**: Achieve 50%+ line coverage across all service and controller classes.

---

## Phase 8: Frontend Development

**Goal**: Build a React SPA with at least 2 routes, 5+ components, HTTP requests, and two-way binding.

**Requirements**:
- React SPA
- 2+ routes/pages
- 5+ components
- HTTP requests to backend API
- Two-way data binding

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
7. `Login.tsx` - Login/Spotify auth
8. `Navbar.tsx` - Navigation component

**Create Directory Structure**:
```
frontend/src/
├── components/
│   ├── Dashboard.tsx
│   ├── TopArtists.tsx
│   ├── TopSongs.tsx
│   ├── CurrentlyPlaying.tsx
│   ├── ListeningHistory.tsx
│   ├── Login.tsx
│   └── Navbar.tsx
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

// Add JWT token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwtToken');
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

**Goal**: Create comprehensive documentation including user stories, wireframes, ERD, and API documentation.

**Required Documents**:
- README.md (root)
- DESCRIPTION.md (with user stories, wireframes, ERD)
- API_DOCUMENTATION.md

### Step 9.1: Create Description File

**Location**: `documentation/DESCRIPTION.md`

**Include**:
1. **App Description**: What the app does, its purpose, features
2. **User Stories**: Format: "As a ____, I want to ____, so that I can ____"
3. **Wireframes**: Frontend UI mockups (can be hand-drawn or digital)
4. **ERD**: Entity Relationship Diagram showing all tables and relationships
5. **Technology Stack**: List of technologies used

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
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "listenerId": "user123",
  "user": { ... }
}
```

## Users

### GET /api/users/{id}
Get user profile by ID.

**Path Parameters:**
- `id` (string, required): User ID

**Headers:**
- `Authorization: Bearer <jwt_token>` (required)

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
- All tables (Listener, Song, Artist, Album, Playlist, History)
- Relationships (One-to-Many, Many-to-Many)
- Primary keys
- Foreign keys
- Junction tables (artists_songs, playlists_songs, albums_songs)

### Step 9.4: Create Wireframes

**Tools**: Figma, Balsamiq, or hand-drawn sketches

**Include wireframes for**:
- Login page
- Dashboard
- Top Artists page
- Top Songs page
- Currently Playing page
- Listening History page

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

**Goal**: Implement proper Git workflow with branching strategy, commit conventions, and branch protection.

**Requirements**:
- Feature branch workflow
- Commit message conventions
- Protected main branch
- Proper .gitignore

### Step 10.1: Set Up Branching Strategy

**Strategy**: Feature branch workflow

**Branches**:
- `main` - Production-ready code (protected)
- `develop` - Integration branch
- `feature/*` - Feature branches (e.g., `feature/jwt-integration`, `feature/exception-handling`)
- `bugfix/*` - Bug fix branches
- `hotfix/*` - Critical production fixes

**Commands**:
```bash
# Create feature branch
git checkout -b feature/jwt-integration

# Work on feature
git add .
git commit -m "feat: integrate JWT token generation in OAuth callback"

# Push to remote
git push origin feature/jwt-integration

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
feat(auth): integrate JWT token generation in OAuth callback
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

### Step 10.4: Verify .gitignore

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

### ✅ Week 1-2: Foundation & Integration (COMPLETED)
- [x] Complete Phase 1 (Database)
- [x] Complete Phase 2 (Backend Foundation)
- [x] Complete Phase 3 (Spotify API)
- [x] Complete Phase 4 (Service Layer)
- [x] Complete Phase 5 (JWT Infrastructure)

### 🚧 Week 3: Security & Error Handling (CURRENT)
- [ ] Complete Phase 5 (JWT Integration) - **IMMEDIATE PRIORITY**
- [ ] Complete Phase 6 (Exception Handling)
- [ ] Start Phase 7 (Testing)

### 📋 Week 4: Testing & Frontend
- [ ] Complete Phase 7 (Testing) - 50%+ coverage
- [ ] Complete Phase 8 (Frontend Development)
- [ ] Integration testing

### 📋 Week 5: Documentation & Polish
- [ ] Complete Phase 9 (Documentation)
- [ ] Complete Phase 10 (Git workflow)
- [ ] Final testing
- [ ] Presentation preparation

---

## Common Issues & Solutions

### Issue: JWT Token Not Generated After OAuth
**Solution**: 
- Verify `JwtUtil` is injected in `SpotifyAuthController`
- Check that `jwtUtil.generateToken(spotifyId)` is called in callback
- Verify JWT properties in `application.properties`

### Issue: 401 Unauthorized on Protected Endpoints
**Solution**:
- Verify JWT token is included in `Authorization: Bearer <token>` header
- Check token expiration time
- Verify `JwtAuthenticationFilter` is properly configured
- Check `SecurityConfig` allows the endpoint

### Issue: CORS Errors
**Solution**:
- Verify CORS configuration in `SecurityConfig`
- Check frontend URL matches allowed origins
- Ensure `@CrossOrigin` annotations are correct

### Issue: Database Connection Failed
**Solution**: 
- Verify Docker container is running: `docker ps`
- Check `application.properties` credentials
- Verify PostgreSQL is accessible on port 5432

### Issue: Test Coverage Below 50%
**Solution**:
- Write tests for all service methods (CRUD operations)
- Test error cases and edge cases
- Add controller tests with MockMvc
- Run `mvn clean test jacoco:report` to check coverage

---

## Resources

- [Spotify Web API Documentation](https://developer.spotify.com/documentation/web-api)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [JWT.io](https://jwt.io/) - JWT debugging tool
- [Mockito Documentation](https://site.mockito.org/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)

---

## Summary

### ✅ Completed
- Database setup and configuration
- Backend foundation with service layer
- Spotify API integration
- JWT infrastructure (utilities, filter, security config)

### 🎯 Next Steps (Priority Order)
1. **Complete JWT Integration** - Update `SpotifyAuthController` to generate JWT tokens
2. **Exception Handling** - Create custom exceptions and global handler
3. **Testing** - Achieve 50%+ test coverage
4. **Frontend Development** - Build React SPA
5. **Documentation** - Create user stories, wireframes, ERD, API docs
6. **Git Workflow** - Implement branching strategy and commit conventions

**Good luck with your project! 🎵**
