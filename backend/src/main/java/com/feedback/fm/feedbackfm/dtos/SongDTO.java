package com.feedback.fm.feedbackfm.dtos;

import java.util.List;

public record SongDTO(
        String songId,
        String name,
        String href,
        Integer durationMs,
        List<String> artistIds,
        List<String> albumIds
) {}
