package com.feedback.fm.feedbackfm.dtos;

public record ListenerDTO(
        String listenerId,
        String displayName,
        String email,
        String country,
        String href,
        Long totalListeningTimeMs,
        Integer totalSongsPlayed
) {
    // Constructor with default values for backward compatibility
    public ListenerDTO(String listenerId, String displayName, String email, String country, String href) {
        this(listenerId, displayName, email, country, href, 0L, 0);
    }
}
