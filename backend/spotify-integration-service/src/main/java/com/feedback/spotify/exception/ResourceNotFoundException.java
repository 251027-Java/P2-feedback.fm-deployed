package com.feedback.spotify.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, String id) {
        super(resource + " not found with id: " + id);
    }
}