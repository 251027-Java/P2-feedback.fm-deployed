package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.PlaylistDTO;

import java.util.List;
import java.util.Optional;

public interface PlaylistService {
    List<PlaylistDTO> getAllPlaylists();
    Optional<PlaylistDTO> getById(String id);
    List<PlaylistDTO> findByName(String name);
    List<PlaylistDTO> searchByName(String namePart);
    List<PlaylistDTO> findByOwnerId(String ownerId);
    List<PlaylistDTO> findByOwnerEmail(String email);
    List<PlaylistDTO> findPublicPlaylists();
    PlaylistDTO create(PlaylistDTO dto);
    PlaylistDTO update(String id, PlaylistDTO dto);
    void delete(String id);
}
