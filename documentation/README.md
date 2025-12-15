# feedback.fm

A full-stack web application that allows users to view their Spotify statistics, including top artists, songs, albums, currently playing tracks, and listening history. Built with React, Spring Boot, and PostgreSQL.

## Technologies Used

### Frontend
- React 19
- TypeScript
- Vite
- React Router DOM
- Axios
- Tailwind CSS (optional styling)

### Backend
- Java 25
- Spring Boot 4.0
- Spring Data JPA
- Spring Security
- JWT (JSON Web Tokens)
- Maven
- JUnit 5 & Mockito
- Spotify Web API

### Database
- PostgreSQL 16
- Docker
- Docker Compose

## Prerequisites

Before running the application, ensure you have the following installed:

- **Node.js** v16 or higher
- **npm** (comes with Node.js)
- **Java JDK** 25 or higher
- **Maven** v3.8 or higher
- **Docker** and Docker Compose
- **Git**
- **Spotify Developer Account** (for API credentials)
  - Client ID and Client Secret from [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/251027-Java/P1-feedback.fm
cd P1-feedback.fm
```

### 2. Set Up Database

Start the PostgreSQL database using Docker Compose:

```bash
cd database
docker-compose up -d
```

This will:
- Start PostgreSQL on port `5432`
- Start pgAdmin on port `5050`
- Create the database `spotifydb` with user `spotify_user`

**Verify the database is running:**
```bash
docker ps
```

**Access pgAdmin (optional):**
- Navigate to `http://localhost:5050`
- Login with: `admin@admin.com` / `admin`
- Add server: `spotify_db`, port `5432`, user `spotify_user`, password `spotify_pass`

**Initialize database schema:**
The schema is automatically created by Hibernate on first run, or you can manually run the SQL scripts from `database/init.sql`.

### 3. Backend Setup

Navigate to the backend directory:

```bash
cd backend
```

**Configure Spotify API credentials:**

Update `src/main/resources/application.properties` with your Spotify credentials:

```properties
spotify.client.id=YOUR_CLIENT_ID
spotify.client.secret=YOUR_CLIENT_SECRET
```

Or set environment variables:
```bash
export SPOTIFY_CLIENT_ID=your_client_id
export SPOTIFY_CLIENT_SECRET=your_client_secret
```

**Important:** Add the redirect URI in your Spotify Developer Dashboard:
- Go to https://developer.spotify.com/dashboard
- Click on your app → Edit Settings
- Add Redirect URI: `http://127.0.0.1:8080/api/auth/callback`
- **Note:** Spotify no longer accepts `localhost` - you must use `127.0.0.1` instead
- Save changes

**Build and run the backend:**

**Option 1: Using Maven Wrapper (Recommended - No installation needed)**
```bash
# Windows (PowerShell or Command Prompt)
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run

# Or if you're using Git Bash
./mvnw clean install
./mvnw spring-boot:run
```

**Option 2: Using Maven (if installed)**
```bash
mvn clean install
mvn spring-boot:run
```

The backend API will be available at `http://localhost:8080`

**Verify the backend is running:**
- Access `http://localhost:8080/api/auth/login` in your browser - you should see a JSON response
- **Note:** Accessing `http://localhost:8080/` will show a 403 error - this is expected! The root path requires authentication. Use the API endpoints instead.

**Run tests with coverage:**
```bash
# Using Maven Wrapper
.\mvnw.cmd clean test jacoco:report

# Or using Maven
mvn clean test jacoco:report
```

View coverage report at: `backend/target/site/jacoco/index.html`

**Note:** If you get "mvn not found" error, use the Maven Wrapper (`mvnw.cmd` on Windows) instead. It's included in the project and doesn't require Maven installation.

### 4. Frontend Setup

Navigate to the frontend directory:

```bash
cd frontend
```

**Install dependencies:**
```bash
npm install
```

**Run the development server:**
```bash
npm run dev
```

The frontend will be available at `http://localhost:3000`

**Build for production:**
```bash
npm run build
```

## Running the Application

1. **Start the database:**
   ```bash
   cd database
   docker-compose up -d
   ```

2. **Start the backend:**
   ```bash
   cd backend
   # Windows
   .\mvnw.cmd spring-boot:run
   # Or if Maven is installed
   mvn spring-boot:run
   ```
   Wait for: `Started FeedbackFmApplication`

3. **Start the frontend:**
   ```bash
   cd frontend
   npm run dev
   ```

4. **Access the application:**
   - Open your browser to `http://localhost:3000`
   - Click "Login with Spotify"
   - Grant permissions when redirected to Spotify
   - You'll be redirected back with authentication
   - Navigate through the app to view your Spotify statistics

## Project Structure

```
P1-feedback.fm/
├── backend/                 # Spring Boot backend
│   ├── src/
│   │   ├── main/java/      # Java source code
│   │   │   └── com/feedback/fm/feedbackfm/
│   │   │       ├── controller/    # REST controllers
│   │   │       ├── service/       # Business logic
│   │   │       ├── repository/    # Data access layer
│   │   │       ├── model/         # Entity classes
│   │   │       ├── dtos/          # Data Transfer Objects
│   │   │       ├── exception/     # Custom exceptions
│   │   │       └── security/      # JWT & Security config
│   │   └── test/           # Unit tests
│   └── pom.xml             # Maven dependencies
├── frontend/               # React frontend
│   ├── src/
│   │   ├── components/     # React components
│   │   ├── services/       # API service
│   │   └── App.tsx         # Main app component
│   └── package.json        # npm dependencies
├── database/               # Database setup
│   ├── docker-compose.yml  # Docker configuration
│   └── init.sql           # Database schema
└── README.md              # This file
```

## API Endpoints

### Authentication
- `GET /api/auth/login` - Get Spotify authorization URL
- `GET /api/auth/callback?code={code}` - Handle OAuth callback
- `POST /api/auth/refresh` - Refresh access token

### Users/Listeners
- `GET /api/users/{id}` - Get user profile
- `GET /api/users/{id}/dashboard` - Get dashboard data
- `GET /api/users/{id}/stats` - Get user statistics
- `PUT /api/users/{id}` - Update user profile
- `DELETE /api/users/{id}` - Delete user account

### Artists
- `GET /api/artists` - Get all artists
- `GET /api/artists/{id}` - Get artist by ID
- `GET /api/artists/top?time_range={range}` - Get top artists
- `POST /api/artists` - Create artist
- `PUT /api/artists/{id}` - Update artist
- `DELETE /api/artists/{id}` - Delete artist

### Songs
- `GET /api/songs` - Get all songs
- `GET /api/songs/{id}` - Get song by ID
- `GET /api/songs/top?time_range={range}` - Get top songs
- `GET /api/songs/currently-playing` - Get currently playing track
- `POST /api/songs` - Create song
- `PUT /api/songs/{id}` - Update song
- `DELETE /api/songs/{id}` - Delete song

### History
- `GET /api/history?limit={limit}` - Get listening history
- `GET /api/history/recent?listenerId={id}&limit={limit}` - Get recent history
- `POST /api/history` - Add history record
- `DELETE /api/history/{id}` - Delete history record

### Playlists & Albums
- Similar CRUD endpoints available

For detailed API documentation, see `DESCRIPTION.md`.

## Testing

### Backend Tests
```bash
cd backend
# Using Maven Wrapper
.\mvnw.cmd test
# Or using Maven
mvn test
```

### Test Coverage
```bash
# Using Maven Wrapper
.\mvnw.cmd clean test jacoco:report
# Or using Maven
mvn clean test jacoco:report
```
View report at: `backend/target/site/jacoco/index.html`

**Target:** 50%+ line coverage

## Troubleshooting

### Backend Shows 403 Forbidden
- **This is normal!** The root path `/` requires authentication
- The backend is working correctly - test with API endpoints like `http://localhost:8080/api/auth/login`
- All API endpoints are under `/api/**` - the root path is protected by Spring Security

### Database Connection Issues
- Verify Docker container is running: `docker ps`
- Check PostgreSQL logs: `docker logs spotify_db`
- Verify connection settings in `application.properties`

### Spotify OAuth Issues

**Error: "INVALID_CLIENT: Invalid redirect URI" or "This redirect URI is not secure"**

**Important:** As of November 2025, Spotify no longer accepts `localhost` in redirect URIs. You must use `127.0.0.1` instead.

This error occurs when the redirect URI configured in your Spotify Developer Dashboard doesn't match what the application is sending. Follow these steps:

1. **Go to Spotify Developer Dashboard:**
   - Navigate to https://developer.spotify.com/dashboard
   - Log in with your Spotify account
   - Click on your app (the one with Client ID: `b7b4d4548e654e9baa16896cbe201097`)

2. **Click "Edit Settings"** (or the Settings icon)

3. **Add the Redirect URI:**
   - Scroll down to "Redirect URIs"
   - Click "Add" or the "+" button
   - Enter exactly: `http://127.0.0.1:8080/api/auth/callback`
   - **Important:** The URI must match EXACTLY:
     - Use `http://127.0.0.1` (NOT `localhost` - Spotify no longer accepts `localhost`)
     - Use `http://` (not `https://`) for loopback addresses
     - No trailing slash at the end
     - All lowercase
     - Exact path: `/api/auth/callback`

4. **Save Changes:**
   - Click "Add" if it's a new URI
   - Click "Save" or "Save Settings" at the bottom

5. **Wait a few seconds** for the changes to propagate (Spotify's servers may take 30-60 seconds to update)

6. **Try logging in again**

**Other Spotify OAuth Issues:**
- Verify redirect URI matches exactly in Spotify Dashboard (case-sensitive, no trailing slashes)
- Check that Client ID and Secret are correct in `application.properties`
- Ensure you're using the correct Spotify Developer account that owns the app
- If you recently changed the redirect URI, wait a minute and try again

### Port Conflicts
- Backend default: `8080` - change in `application.properties`
- Frontend default: `3000` - change in `vite.config.ts`
- Database: `5432` - change in `docker-compose.yml`

### CORS Errors
- Ensure backend CORS is configured for `http://localhost:3000`
- Check `SecurityConfig.java` and `FeedbackFmApplication.java`

## Contributing

This is a team project for educational purposes. Please follow the branching strategy:
- Create feature branches: `feature/your-feature-name`
- Submit pull requests for review
- No direct commits to `main` branch

## License

This project is created for educational purposes as part of a training program.

## Support

For issues or questions, please contact the development team or refer to the `DEVELOPMENT_GUIDE.md` for detailed implementation documentation.
