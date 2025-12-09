package com.feedback.fm.feedbackfm.dtos;

public record ListenerDTO(
        String listenerId,
        String displayName,
        String email,
        String country,
        String href
) {}
