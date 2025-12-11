package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.AlbumDTO;

import java.util.List;
import java.util.Optional;

public interface AlbumService {
    List<AlbumDTO> getAllAlbums();
    Optional<AlbumDTO> getById(String id);
    List<AlbumDTO> searchByTitle(String titlePart);
    List<AlbumDTO> findByReleaseYear(Integer releaseYear);
    List<AlbumDTO> findByArtistId(String artistId);
    AlbumDTO create(AlbumDTO dto);
    AlbumDTO update(String id, AlbumDTO dto);
    void delete(String id);
}
