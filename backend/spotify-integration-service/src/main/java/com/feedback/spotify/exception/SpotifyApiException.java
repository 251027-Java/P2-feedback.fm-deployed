package com.feedback.spotify.exception;

public class SpotifyApiException extends RuntimeException {
    public SpotifyApiException(String message) {
        super(message);
    }
    
    public SpotifyApiException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public SpotifyApiException(String endpoint, int statusCode) {
        super("Spotify API error at " + endpoint + " - Status code: " + statusCode);
    }
}
