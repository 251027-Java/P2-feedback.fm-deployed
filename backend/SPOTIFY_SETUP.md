# Spotify API Integration Setup

## How Your Client ID and Secret Work

Your Spotify credentials are used in the OAuth 2.0 authentication flow to securely access the Spotify Web API.

### Credentials Configuration

**Client ID**: `YOUR_CLIENT_ID` (get from Spotify Developer Dashboard)  
**Client Secret**: `YOUR_CLIENT_SECRET` (get from Spotify Developer Dashboard)

These are configured in `src/main/resources/application.properties`:

```properties
spotify.client.id=${SPOTIFY_CLIENT_ID}
spotify.client.secret=${SPOTIFY_CLIENT_SECRET}
spotify.redirect.uri=http://127.0.0.1:8080/api/auth/callback
```
### How They're Used in the OAuth Flow

#### 1. **Authorization URL Generation** (`SpotifyAuthService.getAuthorizationUrl()`)
   - Uses **Client ID** to build the authorization URL
   - Users are redirected to Spotify to grant permissions
   - Example: `https://accounts.spotify.com/authorize?client_id=YOUR_CLIENT_ID&...`

#### 2. **Token Exchange** (`SpotifyAuthService.exchangeCodeForToken()`)
   - Uses **both Client ID and Secret** for Basic Authentication
   - Encodes them as: `Base64(clientId:clientSecret)`
   - Exchanges the authorization code for an access token
   - This is the secure step that validates your app with Spotify

#### 3. **Token Refresh** (`SpotifyAuthService.refreshToken()`)
   - Uses **both Client ID and Secret** again for Basic Authentication
   - Refreshes expired access tokens using the refresh token

### Important: Redirect URI Configuration

**You MUST configure this redirect URI in your Spotify Developer Dashboard:**

1. Go to https://developer.spotify.com/dashboard
2. Click on your app
3. Click "Edit Settings"
4. Add this Redirect URI: `http://127.0.0.1:8080/api/auth/callback`
   - **Note:** Spotify no longer accepts `localhost` - use `127.0.0.1` instead
5. Save changes

**Without this, the OAuth callback will fail!**

### Security Best Practices

⚠️ **For Production:**
- **DO NOT** commit credentials to Git
- **ALWAYS** use environment variables:
  ```bash
  export SPOTIFY_CLIENT_ID=your_client_id_here
  export SPOTIFY_CLIENT_SECRET=your_client_secret_here
  ```
- The `application.properties` requires environment variables to be set
- Consider using a secrets management service (AWS Secrets Manager, Azure Key Vault, etc.)

### OAuth Flow Diagram

```
1. User clicks "Login with Spotify"
   ↓
2. Frontend calls: GET /api/auth/login
   ↓
3. Backend generates authorization URL (uses Client ID)
   ↓
4. User redirected to Spotify → grants permissions
   ↓
5. Spotify redirects to: /api/auth/callback?code=AUTHORIZATION_CODE
   ↓
6. Backend exchanges code for tokens (uses Client ID + Secret)
   ↓
7. Backend gets user profile from Spotify API
   ↓
8. Backend creates/updates user in database
   ↓
9. Backend generates JWT token and returns to frontend
   ↓
10. Frontend stores JWT token for authenticated API calls
```

### Testing the Integration

1. Start your backend server
2. Navigate to `http://localhost:3000` (frontend)
3. Click "Login with Spotify"
4. You should be redirected to Spotify's authorization page
5. After granting permissions, you'll be redirected back with a token

### Troubleshooting

**Error: "Invalid redirect URI" or "This redirect URI is not secure"**
- Make sure `http://127.0.0.1:8080/api/auth/callback` is added in Spotify Dashboard
- **Important:** Spotify no longer accepts `localhost` - use `127.0.0.1` instead (as of November 2025)

**Error: "Invalid client"**
- Verify Client ID and Secret are correct
- Check for extra spaces or typos

**Error: "Invalid authorization code"**
- Authorization codes expire quickly (usually within 10 minutes)
- Make sure the redirect URI matches exactly

### API Scopes Used

The application requests these scopes:
- `user-read-private` - Read user's profile information
- `user-read-email` - Read user's email address
- `user-read-recently-played` - Access recently played tracks
- `user-top-read` - Read user's top artists and tracks
- `user-read-currently-playing` - Read user's currently playing track

These are configured in `SpotifyAuthService.getAuthorizationUrl()`.
