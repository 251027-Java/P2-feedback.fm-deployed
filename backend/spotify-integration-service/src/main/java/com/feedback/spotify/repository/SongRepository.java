package com.feedback.spotify.repository;

import com.feedback.spotify.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, String> {
    // Find songs by name
    List<Song> findByName(String name);

    // Find songs by partial name (case-insensitive)
    List<Song> findByNameContainingIgnoreCase(String namePart);

    // Find songs by duration
    List<Song> findByDurationMs(Integer duration);

    // Find songs shorter than a specific duration
    List<Song> findByDurationMsLessThan(Integer duration);

    // Find songs longer than a specific duration
    List<Song> findByDurationMsGreaterThan(Integer duration);

    // Find songs within a range of durations
    List<Song> findByDurationMsBetween(Integer minDuration, Integer maxDuration);  

    // Find songs by href
    List<Song> findByHref(String href);

    // Find songs by a specific artist's name
    List<Song> findByArtists_Name(String artistName);
}