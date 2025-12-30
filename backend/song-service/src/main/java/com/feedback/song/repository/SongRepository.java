package com.feedback.song.repository;

import com.feedback.song.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByTitleContainingIgnoreCase(String q);
}
