package com.feedback.fm.feedbackfm.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String username, String reason) {
        super("Authentication failed for user '" + username + "': " + reason);
    }
}
