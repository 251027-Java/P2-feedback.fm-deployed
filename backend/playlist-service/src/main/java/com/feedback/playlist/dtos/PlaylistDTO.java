package com.feedback.playlist.dtos;

import java.util.List;

public record PlaylistDTO(
        String playlistId,
        String name,
        String description,
        String href,
        Boolean isPublic,
        String ownerId,
        List<String> songIds
) {}
