package com.feedback.artist.repository;

import com.feedback.artist.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, String> {
    // Find artists by name
    List<Artist> findByName(String name);

    // Find artists by partial name (case-insensitive)
    List<Artist> findByNameContainingIgnoreCase(String namePart);
}