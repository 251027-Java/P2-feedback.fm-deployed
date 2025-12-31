package com.feedback.album.repository;

import com.feedback.album.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, String> {
    // Find albums by title
    List<Album> findByTitle(String title);

    // Find albums by partial title (case-insensitive)
    List<Album> findByTitleContainingIgnoreCase(String titlePart);

    // Find albums by release year
    List<Album> findByReleaseYear(Integer releaseYear); 
}