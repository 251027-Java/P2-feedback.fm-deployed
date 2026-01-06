Spotify Integration Service

This service handles Spotify OAuth, token exchange/refresh, calls to Spotify Web API, and sync operations.

Build:

```bash
mvn -f backend/spotify-integration-service clean package
```

Run:

```bash
java -jar backend/spotify-integration-service/target/spotify-integration-service-0.0.1.jar
```

Docker:

```bash
docker build -t spotify-integration-service:local backend/spotify-integration-service
```

Endpoints:
- `GET /api/auth/login` — returns Spotify auth URL
- `GET /api/auth/callback?code=...` — callback that exchanges code for token and redirects to frontend
- `POST /api/auth/refresh` — refresh access token

Notes:
- All methods are scaffolded as placeholders; implement real Spotify HTTP calls in `SpotifyApiService` and token exchange in `SpotifyAuthService`.
