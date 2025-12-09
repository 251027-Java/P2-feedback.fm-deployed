package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.PlaylistDTO;
import com.feedback.fm.feedbackfm.model.Playlist;
import com.feedback.fm.feedbackfm.repository.PlaylistRepository;
import com.feedback.fm.feedbackfm.repository.ListenerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlaylistService {

    private final PlaylistRepository repository;
    private final ListenerRepository listenerRepository;

    public PlaylistService(PlaylistRepository repository, ListenerRepository listenerRepository) {
        this.repository = repository;
        this.listenerRepository = listenerRepository;
    }

    public List<PlaylistDTO> getAllPlaylists() {
        return repository.findAll().stream()
                .map(this::playlistToDto)
                .toList();
    }

    public PlaylistDTO getById(String id) {
        Optional<Playlist> result = repository.findById(id);
        return result.map(this::playlistToDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));
    }

    public List<PlaylistDTO> searchByName(String namePart) {
        return repository.findByNameContainingIgnoreCase(namePart).stream()
                .map(this::playlistToDto)
                .toList();
    }

    public List<PlaylistDTO> findByOwnerEmail(String email) {
        return repository.findByOwner_Email(email).stream()
                .map(this::playlistToDto)
                .toList();
    }

    public PlaylistDTO create(PlaylistDTO dto) {
        Playlist playlist = new Playlist();
        playlist.setPlaylistId(dto.playlistId());
        playlist.setName(dto.name());
        playlist.setDescription(dto.description());
        playlist.setHref(dto.href());
        playlist.setIsPublic(dto.isPublic());

        if (dto.ownerId() != null) {
            listenerRepository.findById(dto.ownerId())
                    .ifPresent(playlist::setOwner);
        }

        return playlistToDto(repository.save(playlist));
    }

    public PlaylistDTO update(String id, PlaylistDTO dto) {
        Playlist playlist = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));

        playlist.setName(dto.name());
        playlist.setDescription(dto.description());
        playlist.setHref(dto.href());
        playlist.setIsPublic(dto.isPublic());

        if (dto.ownerId() != null) {
            listenerRepository.findById(dto.ownerId())
                    .ifPresent(playlist::setOwner);
        }

        return playlistToDto(repository.save(playlist));
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found");
        }
        repository.deleteById(id);
    }

    private PlaylistDTO playlistToDto(Playlist playlist) {
        String ownerId = playlist.getOwner() != null ? playlist.getOwner().getListenerId() : null;

        List<String> songIds = playlist.getSongs().stream()
                .map(song -> song.getSongId())
                .collect(Collectors.toList());

        return new PlaylistDTO(
                playlist.getPlaylistId(),
                playlist.getName(),
                playlist.getDescription(),
                playlist.getHref(),
                playlist.getIsPublic(),
                ownerId,
                songIds
        );
    }
}

