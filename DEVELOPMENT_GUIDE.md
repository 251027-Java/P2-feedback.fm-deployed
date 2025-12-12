# Development Guide: feedback.fm

A comprehensive step-by-step guide for developing a full-stack Spotify statistics application similar to Last.fm.

## Table of Contents
1. [Project Overview](#project-overview)
2. [Next Steps - Priority Order](#next-steps---priority-order)
3. [Phase 6.3: Update Services to Use Custom Exceptions](#phase-63-update-services-to-use-custom-exceptions)
4. [Phase 7: Testing](#phase-7-testing)
5. [Phase 8: Frontend Development](#phase-8-frontend-development)
6. [Phase 9: Documentation](#phase-9-documentation)
7. [Phase 10: Git Workflow & Best Practices](#phase-10-git-workflow--best-practices)

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
- ✅ **Phase 5: JWT Integration** - JWT utilities, filter, security config, and OAuth callback integration completed
- ✅ **Phase 6.1: Custom Exceptions** - All custom exception classes created
- ✅ **Phase 6.2: Global Exception Handler** - GlobalExceptionHandler implemented

#### 🚧 In Progress / Next Steps
- ⚠️ **Phase 6.3: Update Services** - Replace ResponseStatusException with custom exceptions in all service implementations
- ⚠️ **Phase 7: Testing** - Unit and integration tests needed (50%+ coverage, currently only 1 test file)
- ⚠️ **Phase 8: Frontend Development** - React application needed
- ⚠️ **Phase 9: Documentation** - API docs, ERD, wireframes needed
- ⚠️ **Phase 10: Git Workflow** - Branching strategy and best practices

---

## Next Steps - Priority Order

### 🎯 IMMEDIATE PRIORITY: Phase 6.3 - Update Services to Use Custom Exceptions

**Status**: Custom exceptions and global handler are created, but services still use `ResponseStatusException`.

**What's Done**:
- ✅ Custom exception classes created (`ResourceNotFoundException`, `InvalidRequestException`, `SpotifyApiException`, `AuthenticationException`)
- ✅ `GlobalExceptionHandler` implemented and ready to handle custom exceptions

**What's Missing**:
- ❌ Services still throw `ResponseStatusException` instead of custom exceptions
- ❌ Inconsistent error handling across the application

**Action Required**: Replace all `ResponseStatusException` usages with appropriate custom exceptions in service implementations.

**Files to Update** (8 files):
- `ListenerServiceImpl.java`
- `SongServiceImpl.java`
- `ArtistServiceImpl.java`
- `AlbumServiceImpl.java`
- `PlaylistServiceImpl.java`
- `HistoryServiceImpl.java`
- `SpotifyAuthService.java`
- `SpotifyApiService.java`

---

## Phase 6.3: Update Services to Use Custom Exceptions

**Goal**: Replace `ResponseStatusException` with custom exceptions in all service implementations to ensure consistent error handling.

**Why This is Important**:
- Provides consistent error response format across all endpoints
- Better error messages for frontend
- Easier debugging and logging
- Required for project completion

### Step 6.3.1: Update Import Statements

**Add imports** for custom exceptions in each service file:
```java
import com.feedback.fm.feedbackfm.exception.ResourceNotFoundException;
import com.feedback.fm.feedbackfm.exception.InvalidRequestException;
import com.feedback.fm.feedbackfm.exception.SpotifyApiException;
import com.feedback.fm.feedbackfm.exception.AuthenticationException;
```

**Remove** (if present):
```java
import org.springframework.web.server.ResponseStatusException;
```

### Step 6.3.2: Replace Exception Types

**Mapping Guide**:

| Old (ResponseStatusException) | New (Custom Exception) | HTTP Status |
|-------------------------------|------------------------|-------------|
| `ResponseStatusException(HttpStatus.NOT_FOUND, ...)` | `ResourceNotFoundException` | 404 |
| `ResponseStatusException(HttpStatus.BAD_REQUEST, ...)` | `InvalidRequestException` | 400 |
| `ResponseStatusException(HttpStatus.CONFLICT, ...)` | `InvalidRequestException` | 400 |
| `ResponseStatusException(HttpStatus.UNAUTHORIZED, ...)` | `AuthenticationException` | 401 |
| Spotify API errors | `SpotifyApiException` | 503 |

### Step 6.3.3: Update Each Service File

**Example** (in `ListenerServiceImpl.java`):

**Before**:
```java
if (id == null || id.isBlank()) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
        "Listener ID cannot be null or blank");
}

Listener listener = repository.findById(id)
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
        "Listener not found with id: " + id));

if (repository.existsById(dto.listenerId())) {
    throw new ResponseStatusException(HttpStatus.CONFLICT, 
        "Listener with ID '" + dto.listenerId() + "' already exists");
}
```

**After**:
```java
if (id == null || id.isBlank()) {
    throw new InvalidRequestException("Listener ID cannot be null or blank");
}

Listener listener = repository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("Listener", id));

if (repository.existsById(dto.listenerId())) {
    throw new InvalidRequestException(
        "Listener with ID '" + dto.listenerId() + "' already exists");
}
```

### Step 6.3.4: Files to Update

**Priority 1 - Core Services**:
1. **`ListenerServiceImpl.java`**
   - Replace NOT_FOUND → `ResourceNotFoundException("Listener", id)`
   - Replace BAD_REQUEST → `InvalidRequestException(message)`
   - Replace CONFLICT → `InvalidRequestException(message)`

2. **`SongServiceImpl.java`**
   - Replace NOT_FOUND → `ResourceNotFoundException("Song", id)`
   - Replace BAD_REQUEST → `InvalidRequestException(message)`
   - Replace CONFLICT → `InvalidRequestException(message)`

3. **`ArtistServiceImpl.java`**
   - Replace NOT_FOUND → `ResourceNotFoundException("Artist", id)`
   - Replace BAD_REQUEST → `InvalidRequestException(message)`
   - Replace CONFLICT → `InvalidRequestException(message)`

4. **`AlbumServiceImpl.java`**
   - Replace NOT_FOUND → `ResourceNotFoundException("Album", id)`
   - Replace BAD_REQUEST → `InvalidRequestException(message)`
   - Replace CONFLICT → `InvalidRequestException(message)`

5. **`PlaylistServiceImpl.java`**
   - Replace NOT_FOUND → `ResourceNotFoundException("Playlist", id)`
   - Replace BAD_REQUEST → `InvalidRequestException(message)`
   - Replace CONFLICT → `InvalidRequestException(message)`

6. **`HistoryServiceImpl.java`**
   - Replace NOT_FOUND → `ResourceNotFoundException("History", id)`
   - Replace BAD_REQUEST → `InvalidRequestException(message)`

**Priority 2 - Spotify Services**:
7. **`SpotifyAuthService.java`**
   - Replace API errors → `SpotifyApiException(message)`
   - Replace authentication errors → `AuthenticationException(message)`

8. **`SpotifyApiService.java`**
   - Replace API errors → `SpotifyApiException(message, cause)`
   - Use `SpotifyApiException(message, cause)` for exceptions with underlying causes

### Step 6.3.5: Testing the Changes

**After updating services**:
1. Compile the project: `mvn clean compile`
2. Start the backend server
3. Test endpoints that should throw exceptions:
   - GET non-existent resource → Should return 404 with consistent format
   - POST invalid data → Should return 400 with consistent format
   - POST duplicate resource → Should return 400 with consistent format
4. Verify error responses match the format from `GlobalExceptionHandler`:
   ```json
   {
     "timestamp": "2024-01-15T10:30:00",
     "status": 404,
     "error": "Resource Not Found",
     "message": "Listener not found with id: user123",
     "path": "/api/users/user123"
   }
   ```

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
import com.feedback.fm.feedbackfm.exception.InvalidRequestException;
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
        
        assertThrows(InvalidRequestException.class, () -> listenerService.create(testListenerDTO));
    }
    
    @Test
    void update_WhenNotFound_ThrowsResourceNotFoundException() {
        when(listenerRepository.findById("nonexistent")).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, 
            () -> listenerService.update("nonexistent", testListenerDTO));
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
- `feature/*` - Feature branches (e.g., `feature/exception-handling`, `feature/testing`)
- `bugfix/*` - Bug fix branches
- `hotfix/*` - Critical production fixes

**Commands**:
```bash
# Create feature branch
git checkout -b feature/exception-handling

# Work on feature
git add .
git commit -m "feat(exception): replace ResponseStatusException with custom exceptions"

# Push to remote
git push origin feature/exception-handling

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
feat(exception): replace ResponseStatusException with custom exceptions
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
- [x] Complete Phase 5 (JWT Integration)
- [x] Complete Phase 6.1 (Custom Exceptions)
- [x] Complete Phase 6.2 (Global Exception Handler)

### 🚧 Week 3: Error Handling & Testing (CURRENT)
- [ ] Complete Phase 6.3 (Update Services to Use Custom Exceptions) - **IMMEDIATE PRIORITY**
- [ ] Start Phase 7 (Testing) - Aim for 50%+ coverage

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

### Issue: Custom Exceptions Not Being Caught
**Solution**: 
- Verify `GlobalExceptionHandler` is in the correct package (`com.feedback.fm.feedbackfm.exception`)
- Ensure `@RestControllerAdvice` annotation is present
- Check that services are throwing custom exceptions, not `ResponseStatusException`
- Restart the Spring Boot application after changes

### Issue: Inconsistent Error Response Format
**Solution**:
- Verify all services use custom exceptions
- Check that `GlobalExceptionHandler` handles all exception types
- Ensure no controllers are catching exceptions and returning custom responses

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
- JWT infrastructure and OAuth integration
- Custom exception classes
- Global exception handler

### 🎯 Next Steps (Priority Order)
1. **Phase 6.3: Update Services** - Replace `ResponseStatusException` with custom exceptions in all service implementations
2. **Phase 7: Testing** - Achieve 50%+ test coverage
3. **Phase 8: Frontend Development** - Build React SPA
4. **Phase 9: Documentation** - Create user stories, wireframes, ERD, API docs
5. **Phase 10: Git Workflow** - Implement branching strategy and commit conventions

**Good luck with your project! 🎵**
