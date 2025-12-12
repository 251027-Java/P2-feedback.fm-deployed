package com.feedback.fm.feedbackfm.exception;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
    
    public InvalidRequestException(String field, String value) {
        super("Invalid request - " + field + ": " + value);
    }
}
