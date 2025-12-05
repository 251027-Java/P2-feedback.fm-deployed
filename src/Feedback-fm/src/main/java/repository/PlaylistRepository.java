package repository;

import feedbackmodel.Playlist;
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

    // Find playlists by listener's display name
    List<Playlist> findByListener_DisplayName(String displayName);

    // Find playlists by listener's email
    List<Playlist> findByListener_Email(String email);
}