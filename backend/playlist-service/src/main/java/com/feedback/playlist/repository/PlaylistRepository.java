package com.feedback.playlist.repository;

import com.feedback.playlist.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, String> {
    // Find playlists by name
    List<Playlist> findByName(String name);

    // Find playlists by partial name (case-insensitive)
    List<Playlist> findByNameContainingIgnoreCase(String namePart);

    // Find playlists by description
    List<Playlist> findByDescription(String description);

    // Find playlists by partial description (case-insensitive)
    List<Playlist> findByDescriptionContainingIgnoreCase(String descriptionPart);

    // kenneth: changed this to be owner instead of listener to match up with Omar's model
    // Find playlists by owner's display name
    List<Playlist> findByOwner_DisplayName(String displayName);

    // Find playlists by owner's email
    List<Playlist> findByOwner_Email(String email);
}