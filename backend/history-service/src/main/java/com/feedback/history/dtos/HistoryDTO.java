package com.feedback.history.dtos;

import java.time.LocalDateTime;

public record HistoryDTO(
        Long historyId,
        LocalDateTime playedAt,
        String listenerId,
        String songId
) {}
