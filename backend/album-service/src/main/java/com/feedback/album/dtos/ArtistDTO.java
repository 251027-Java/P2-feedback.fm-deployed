package com.feedback.album.dtos;

import java.util.List;

public record ArtistDTO(
        String artistId,
        String name,
        String href,
        List<String> albumIds
) {}
