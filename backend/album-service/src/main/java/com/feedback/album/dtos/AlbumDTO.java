package com.feedback.album.dtos;

import java.util.List;

public record AlbumDTO(
        String albumId,
        String title,
        Integer releaseYear,
        String href,
        String artistId,
        List<String> songIds
) {}
