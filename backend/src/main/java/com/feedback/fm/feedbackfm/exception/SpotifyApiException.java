package com.feedback.fm.feedbackfm.exception;

public class SpotifyApiException extends RuntimeException {
    public SpotifyApiException(String message) {
        super(message);
    }
    
    public SpotifyApiException(String endpoint, int statusCode) {
        super("Spotify API error at " + endpoint + " - Status code: " + statusCode);
    }
}
