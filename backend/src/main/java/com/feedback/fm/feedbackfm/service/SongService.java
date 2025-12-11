package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.SongDTO;

import java.util.List;
import java.util.Optional;

public interface SongService {
    List<SongDTO> getAllSongs();
    Optional<SongDTO> getById(String id);
    List<SongDTO> findByName(String name);
    List<SongDTO> searchByName(String namePart);
    List<SongDTO> findByArtistName(String artistName);
    List<SongDTO> findByDuration(Integer duration);
    List<SongDTO> findByDurationRange(Integer minDuration, Integer maxDuration);
    SongDTO create(SongDTO dto);
    SongDTO update(String id, SongDTO dto);
    void delete(String id);
}
