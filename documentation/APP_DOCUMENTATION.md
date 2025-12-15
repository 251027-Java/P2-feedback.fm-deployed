# feedback.fm - Application Documentation

## Table of Contents
1. [Overview](#overview)
2. [Features](#features)
3. [Architecture](#architecture)
4. [Implementation Details](#implementation-details)
5. [API Endpoints](#api-endpoints)
6. [Testing](#testing)
7. [User Guide](#user-guide)

---

## Overview

**feedback.fm** is a full-stack web application that provides users with comprehensive insights into their Spotify listening habits. The application integrates with Spotify's Web API to deliver real-time statistics, personalized music recommendations, and detailed listening history analytics.

### Key Technologies
- **Frontend**: React 19.2.3 with TypeScript 5.9.3, Vite 7.2.6
- **Backend**: Spring Boot 4.0.0 with Java 25.0.1
- **Database**: PostgreSQL 16 (Docker)
- **Authentication**: Spotify OAuth 2.0
- **API Integration**: Spotify Web API

### Project Goals
- Provide users with detailed insights into their music preferences
- Track listening history and patterns
- Display real-time currently playing information
- Manage and view playlists
- Analyze top artists and songs across different time periods

---

## Features

### 1. User Authentication
- **Spotify OAuth Integration**: Secure login through Spotify accounts
- **Token Management**: Automatic handling of access and refresh tokens
- **Session Persistence**: User data stored locally for seamless experience

### 2. Dashboard
- **User Profile**: Display Spotify profile information with avatar
- **Listening Statistics**: 
  - Total listening time (hours and minutes)
  - Total songs played
  - Current listening streak
- **Now Playing Widget**: Real-time display of currently playing track with album art
- **Quick Access**: Links to Top Artists and Top Songs
- **Recent Activity**: Display of recently played tracks

### 3. Top Artists
- **Time Range Selection**: View top artists from different periods
  - Short term (last 4 weeks)
  - Medium term (last 6 months)
  - Long term (all time)
- **Search Functionality**: Filter artists by name
- **Sort Options**: 
  - Popularity (default)
  - Name (alphabetical)
- **Artist Cards**: Display with profile images, names, and popularity scores
- **Clickable Links**: Direct links to artist Spotify pages

### 4. Top Songs
- **Time Range Selection**: Same as Top Artists
- **Sort Options**:
  - Popularity (default)
  - Song name
  - Artist name
- **Song Details**: 
  - Track name
  - Artist name
  - Album name (if available)
  - Duration (formatted as MM:SS)
- **Album Artwork**: Visual display of album covers
- **Interactive Cards**: Hover effects with smooth animations

### 5. Playlists
- **View All Playlists**: Fetches all user playlists from Spotify (with pagination)
- **Playlist Cards**: Show name, description, track count, public/private status
- **Track Details Modal**: Click to view all songs in a playlist
- **Song Information**: Track number, name, artist, duration
- **Pagination Support**: Handles users with 50+ playlists

### 6. Listening History
- **Configurable Display**: Dropdown to show 10, 20, 30, 50, 75, or 100 recent tracks
- **Track Counter**: Real-time display of loaded tracks
- **Detailed Information**: Song, artist, album, and playback time
- **Chronological Order**: Most recent plays first
- **Sync with Spotify**: Automatically syncs recent plays to database

### 7. Now Playing
- **Real-Time Updates**: Polls Spotify API every 5 seconds
- **Playback Status**: Shows if track is playing or paused
- **Progress Bar**: Visual representation of track progress
- **Full Track Details**: Song, artist, album, duration
- **Album Artwork**: Large display of current track's album cover
- **Last Updated Timestamp**: Shows when data was last refreshed

---

## Architecture

### System Architecture Diagram

```
┌─────────────────┐
│   React SPA     │
│   (Frontend)    │
│  Port 3000      │
└────────┬────────┘
         │ HTTP/REST
         │
┌────────▼────────────────────────┐
│   Spring Boot Backend           │
│   Port 8080                     │
│                                 │
│  ┌──────────────────────────┐  │
│  │   Controllers            │  │
│  │  - Auth                  │  │
│  │  - User/Listener         │  │
│  │  - Songs                 │  │
│  │  - Artists               │  │
│  │  - Playlists             │  │
│  │  - History               │  │
│  └────────┬─────────────────┘  │
│           │                     │
│  ┌────────▼─────────────────┐  │
│  │   Services               │  │
│  │  - Spotify API Service   │  │
│  │  - Spotify Auth Service  │  │
│  │  - Spotify Sync Service  │  │
│  │  - Business Logic        │  │
│  └────────┬─────────────────┘  │
│           │                     │
│  ┌────────▼─────────────────┐  │
│  │   Repositories (JPA)     │  │
│  │  - Listener              │  │
│  │  - Song                  │  │
│  │  - Artist                │  │
│  │  - History               │  │
│  │  - Playlist              │  │
│  └────────┬─────────────────┘  │
└───────────┼─────────────────────┘
            │ JDBC
┌───────────▼──────────────┐
│   PostgreSQL Database    │
│   Port 5432              │
│   (Docker Container)     │
└──────────────────────────┘

External API:
┌──────────────────────────┐
│   Spotify Web API        │
│   api.spotify.com        │
└──────────────────────────┘
```

### Database Schema (3NF)

```sql
-- Core User Table
listener (
    listener_id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(255),
    profile_image TEXT,
    spotify_href TEXT,
    total_listening_time_ms BIGINT,
    total_songs_played INTEGER
)

-- Artists Table
artist (
    artist_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    popularity INTEGER,
    spotify_href TEXT,
    profile_image TEXT
)

-- Songs Table
song (
    song_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    duration_ms INTEGER,
    popularity INTEGER,
    spotify_href TEXT,
    album_name VARCHAR(255),
    album_image TEXT,
    release_date VARCHAR(50)
)

-- Song-Artist Junction (Many-to-Many)
song_artist (
    song_id VARCHAR(255) REFERENCES song(song_id),
    artist_id VARCHAR(255) REFERENCES artist(artist_id),
    PRIMARY KEY (song_id, artist_id)
)

-- Listening History
history (
    history_id BIGSERIAL PRIMARY KEY,
    listener_id VARCHAR(255) REFERENCES listener(listener_id),
    song_id VARCHAR(255) REFERENCES song(song_id),
    played_at TIMESTAMP NOT NULL,
    duration_ms INTEGER
)

-- Playlists
playlist (
    playlist_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_public BOOLEAN,
    owner_id VARCHAR(255) REFERENCES listener(listener_id),
    image_url TEXT,
    spotify_href TEXT
)

-- Playlist-Song Junction (Many-to-Many)
playlist_song (
    playlist_id VARCHAR(255) REFERENCES playlist(playlist_id),
    song_id VARCHAR(255) REFERENCES song(song_id),
    added_at TIMESTAMP,
    position INTEGER,
    PRIMARY KEY (playlist_id, song_id)
)

-- Albums Table
album (
    album_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    release_date VARCHAR(50),
    image_url TEXT,
    spotify_href TEXT,
    total_tracks INTEGER
)
```

---

## Implementation Details

### Frontend Architecture

#### Component Structure
```
src/
├── components/
│   ├── Dashboard.tsx          # Main dashboard with stats and widgets
│   ├── TopArtists.tsx         # Top artists view with filtering
│   ├── TopSongs.tsx           # Top songs view with sorting
│   ├── Playlists.tsx          # Playlist management and viewing
│   ├── ListeningHistory.tsx   # Historical listening data
│   ├── CurrentlyPlaying.tsx   # Real-time now playing display
│   ├── Navbar.tsx             # Navigation with menu
│   ├── Login.tsx              # Spotify OAuth login
│   ├── AnimatedContent.tsx    # Fade-in animations wrapper
│   ├── LiquidEther.tsx        # Animated background effect
│   └── StaggeredMenu.tsx      # Animated navigation menu
├── services/
│   └── api.ts                 # API service layer with axios
└── App.tsx                    # Main app with routing
```

#### State Management
- **React Hooks**: `useState`, `useEffect` for local state
- **LocalStorage**: Token and user data persistence
- **API Polling**: Real-time updates for currently playing (5-10 second intervals)

#### Styling Approach
- **Inline Styles**: Component-specific styling with TypeScript
- **Color Scheme**: Spotify green (#1DB954, #1ed760, #18a344) with dark theme (#000)
- **Responsive Design**: Grid layouts with auto-fill and minmax
- **Hover Effects**: Smooth transitions with scale and color changes

### Backend Architecture

#### Layer Structure

**1. Controller Layer**
```java
@RestController
@RequestMapping("/api/...")
@CrossOrigin(origins = "http://localhost:3000")
```
- Handles HTTP requests
- Input validation
- Response formatting
- Spotify token extraction from headers

**2. Service Layer**
```java
@Service
public class SpotifyApiService {
    // Spotify API integration
}
```
- Business logic implementation
- Spotify API communication
- Data transformation
- Error handling

**3. Repository Layer**
```java
@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    // Custom query methods
}
```
- Data access operations
- Custom JPA queries
- Database interactions

#### Key Features Implementation

**Spotify OAuth Flow**
```java
1. User clicks "Login with Spotify"
2. Redirect to Spotify authorization page
3. User grants permissions
4. Spotify redirects back with authorization code
5. Backend exchanges code for access & refresh tokens
6. Tokens stored in localStorage on frontend
7. Access token included in all API requests via X-Spotify-Token header
```

**Data Synchronization**
```java
// SpotifySyncService.java
public void syncRecentlyPlayed(String spotifyToken, String listenerId) {
    // 1. Fetch recently played from Spotify (max 50 tracks)
    // 2. Check for duplicates in database
    // 3. Create/update song and artist records
    // 4. Create history entries
    // 5. Update listener cumulative stats
}
```

**Pagination Handling**
```java
// PlaylistController.java - Fetch ALL playlists
List<Map<String, Object>> allPlaylists = new ArrayList<>();
int limit = 50;
int offset = 0;
boolean hasMore = true;

while (hasMore) {
    Map<String, Object> response = spotifyApiService.getUserPlaylists(token, limit, offset);
    // Process items...
    hasMore = response.get("next") != null;
    offset += limit;
}
```

#### Error Handling
- **401 Unauthorized**: Token expired → redirect to login
- **404 Not Found**: Resource doesn't exist
- **500 Internal Server Error**: Logged with context
- **Try-Catch Blocks**: Graceful degradation with user-friendly messages

---

## API Endpoints

### Authentication
```http
GET /api/auth/login
GET /api/auth/callback?code={authCode}
```

### User/Listener
```http
GET /api/users/{id}
GET /api/users/{id}/dashboard
Headers: X-Spotify-Token: {token}
```

### Artists
```http
GET /api/artists/top?time_range={short_term|medium_term|long_term}
Headers: X-Spotify-Token: {token}
```

### Songs
```http
GET /api/songs/top?time_range={short_term|medium_term|long_term}
GET /api/songs/currently-playing
Headers: X-Spotify-Token: {token}
```

### Playlists
```http
GET /api/playlists/spotify
GET /api/playlists/spotify/{playlistId}/tracks
Headers: X-Spotify-Token: {token}
```

### History
```http
GET /api/history?limit={number}
Headers: X-Spotify-Token: {token}
```

---

## Testing

### Backend Testing Strategy

#### Unit Tests
Located in `src/test/java/com/feedback/fm/feedbackfm/service/`

**Example: PlaylistServiceTest.java**
```java
@SpringBootTest
public class PlaylistServiceTest {
    @Autowired
    private PlaylistService playlistService;
    
    @Test
    public void testCreatePlaylist() {
        PlaylistDTO dto = new PlaylistDTO(...);
        PlaylistDTO created = playlistService.create(dto);
        assertNotNull(created.getPlaylistId());
        assertEquals("Test Playlist", created.getName());
    }
    
    @Test
    public void testFindByOwnerId() {
        List<PlaylistDTO> playlists = playlistService.findByOwnerId("user123");
        assertFalse(playlists.isEmpty());
    }
}
```

**Example: SongServiceTest.java**
```java
@Test
public void testGetTopSongs() {
    // Mock Spotify API response
    List<SongDTO> songs = songService.getTopSongs("token", "short_term");
    assertNotNull(songs);
    assertTrue(songs.size() <= 50);
}
```

#### Integration Tests
- Test full request/response cycles
- Verify database operations
- Test Spotify API integration (with mocked responses)

#### Test Coverage Areas
1. **Service Layer**
   - Business logic validation
   - Data transformation
   - Error handling
   
2. **Repository Layer**
   - Custom query methods
   - JPQL queries
   - Data persistence
   
3. **Controller Layer**
   - Request mapping
   - Response formatting
   - Authorization checks

### Frontend Testing

#### Manual Testing Checklist
- [ ] Login flow completes successfully
- [ ] Dashboard loads with correct user data
- [ ] Top Artists displays for all time ranges
- [ ] Top Songs sorting works correctly
- [ ] Playlists page loads all playlists (50+)
- [ ] Playlist modal shows correct tracks
- [ ] Listening History limit selector works
- [ ] Now Playing updates in real-time
- [ ] Navigation between pages works
- [ ] Logout clears session data

#### Browser Compatibility
- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

### Running Tests

**Backend Unit Tests**
```bash
cd backend
./mvnw test
```

**Backend with Coverage**
```bash
./mvnw clean test jacoco:report
```

**Frontend (if configured)**
```bash
cd frontend
npm test
```

---

## User Guide

### Getting Started

#### 1. First Time Setup
1. Navigate to `http://localhost:3000`
2. Click "Login with Spotify"
3. Authorize the application
4. Redirected to Dashboard

#### 2. Dashboard Overview
- **Profile Section**: Your Spotify username and avatar
- **Now Playing**: Current track (updates every 10 seconds)
- **Stats**: Total listening time, songs played, current streak
- **Top Artists**: Quick preview of your favorite artists
- **Top Songs**: Quick preview of your most played songs

#### 3. Exploring Your Music

**Top Artists**
1. Navigate to "Top Artists" from menu
2. Select time range (4 weeks, 6 months, all time)
3. Use search bar to filter artists
4. Change sorting (Popularity or Name)
5. Click artist cards to open Spotify page

**Top Songs**
1. Navigate to "Top Songs" from menu
2. Select time range
3. Sort by popularity, name, or artist
4. View song details including duration
5. Album artwork displayed for each track

**Playlists**
1. Navigate to "Playlists" from menu
2. Browse all your Spotify playlists
3. Click any playlist to view tracks
4. See track count and public/private status
5. Modal shows full track list with durations

**Listening History**
1. Navigate to "History" from menu
2. Select number of tracks to display (10-100)
3. View recent plays with timestamps
4. See when each track was played

**Now Playing**
1. Navigate to "Now Playing" from menu
2. See currently playing track in large format
3. View progress bar and playback status
4. See full track information
5. Auto-updates every 5 seconds

#### 4. Common Issues

**"No track playing" shown when music is playing**
- Ensure Spotify app is actively playing
- Check that you're logged into the same Spotify account
- Token may have expired - try logging out and back in

**Top Artists/Songs not loading**
- Verify you have listening history on Spotify
- Check time range selection
- Ensure access token is valid

**Playlists not showing**
- Backend needs to be running
- Check that playlists exist in your Spotify account
- Token must have playlist-read permissions

#### 5. Tips & Tricks
- Use **short_term** (4 weeks) to see current music trends
- Use **long_term** (all time) for your overall favorites
- Listening History syncs automatically when viewing Dashboard
- Dashboard widgets are clickable - click Now Playing to go to full page
- Streak counter shows consecutive days with at least one play

### Technical Notes

#### Token Expiration
- Spotify access tokens expire after ~1 hour
- You'll see 401 errors in console when expired
- Simply log out and log back in to refresh

#### Data Refresh Rates
- **Dashboard**: Manual refresh or page reload
- **Now Playing**: Every 10 seconds (Dashboard), 5 seconds (dedicated page)
- **History**: Synced on Dashboard load
- **Top Artists/Songs**: On time range change

#### Performance Considerations
- Playlist page may take longer with 50+ playlists (pagination)
- History with 100 tracks loads more data than 10 tracks
- Now Playing polls frequently - disable if not needed

---

## Future Enhancements

### Planned Features
1. **Data Visualization**
   - Charts showing listening patterns over time
   - Genre distribution pie charts
   - Daily/weekly listening trends

2. **Advanced Analytics**
   - Discover new artists based on listening history
   - Compare stats with friends
   - Year in review summary

3. **Playlist Management**
   - Create playlists within app
   - Add/remove songs from playlists
   - Collaborative playlist features

4. **Export Functionality**
   - Export listening history to CSV
   - Generate shareable stats images
   - Download data for external analysis

5. **Mobile Optimization**
   - Responsive design improvements
   - Touch-friendly interactions
   - Progressive Web App (PWA) support

6. **Settings Page**
   - Customize refresh intervals
   - Privacy controls
   - Theme preferences
   - Data sync preferences

---

## Conclusion

feedback.fm provides a comprehensive solution for Spotify users to gain deeper insights into their listening habits. Built with modern web technologies and following best practices, the application demonstrates:

- **Scalable Architecture**: Clean separation of concerns across frontend and backend
- **Secure Authentication**: OAuth 2.0 integration with Spotify
- **Real-time Data**: Live updates and synchronization
- **User-Centric Design**: Intuitive interface with Spotify's design language
- **Robust Testing**: Comprehensive test coverage for critical functionality
- **Pagination Support**: Handles large datasets efficiently

The application serves as both a functional music statistics tool and a demonstration of full-stack development capabilities.

