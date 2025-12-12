package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.PlaylistDTO;
import com.feedback.fm.feedbackfm.exception.InvalidRequestException;
import com.feedback.fm.feedbackfm.exception.ResourceNotFoundException;
import com.feedback.fm.feedbackfm.model.Listener;
import com.feedback.fm.feedbackfm.model.Playlist;
import com.feedback.fm.feedbackfm.repository.ListenerRepository;
import com.feedback.fm.feedbackfm.repository.PlaylistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository repository;
    private final ListenerRepository listenerRepository;

    public PlaylistServiceImpl(PlaylistRepository repository, ListenerRepository listenerRepository) {
        this.repository = repository;
        this.listenerRepository = listenerRepository;
    }

    @Override
    public List<PlaylistDTO> getAllPlaylists() {
        return repository.findAll().stream()
                .map(this::playlistToDto)
                .toList();
    }

    @Override
    public Optional<PlaylistDTO> getById(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidRequestException("Playlist ID cannot be null or blank");
        }
        return repository.findById(id)
                .map(this::playlistToDto);
    }

    @Override
    public List<PlaylistDTO> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return repository.findByName(name).stream()
                .map(this::playlistToDto)
                .toList();
    }

    @Override
    public List<PlaylistDTO> searchByName(String namePart) {
        if (namePart == null || namePart.isBlank()) {
            return List.of();
        }
        return repository.findByNameContainingIgnoreCase(namePart).stream()
                .map(this::playlistToDto)
                .toList();
    }

    @Override
    public List<PlaylistDTO> findByOwnerId(String ownerId) {
        if (ownerId == null || ownerId.isBlank()) {
            return List.of();
        }
        // Verify owner exists
        if (!listenerRepository.existsById(ownerId)) {
            throw new ResourceNotFoundException("Owner", ownerId);
        }
        return repository.findAll().stream()
                .filter(playlist -> playlist.getOwner() != null 
                    && playlist.getOwner().getListenerId().equals(ownerId))
                .map(this::playlistToDto)
                .toList();
    }

    @Override
    public List<PlaylistDTO> findByOwnerEmail(String email) {
        if (email == null || email.isBlank()) {
            return List.of();
        }
        return repository.findByOwner_Email(email).stream()
                .map(this::playlistToDto)
                .toList();
    }

    @Override
    public List<PlaylistDTO> findPublicPlaylists() {
        return repository.findAll().stream()
                .filter(playlist -> playlist.getIsPublic() != null && playlist.getIsPublic())
                .map(this::playlistToDto)
                .toList();
    }

    @Override
    @Transactional
    public PlaylistDTO create(PlaylistDTO dto) {
        validatePlaylistDTO(dto);
        
        // check existing playlist
        if (repository.existsById(dto.playlistId())) {
            throw new InvalidRequestException("Playlist with ID '" + dto.playlistId() + "' already exists");
        }
        
        Playlist playlist = new Playlist();
        playlist.setPlaylistId(dto.playlistId());
        playlist.setName(dto.name());
        playlist.setDescription(dto.description());
        playlist.setHref(dto.href());
        playlist.setIsPublic(dto.isPublic() != null ? dto.isPublic() : false);

        // validate and set owner if provided
        if (dto.ownerId() != null && !dto.ownerId().isBlank()) {
            Listener owner = listenerRepository.findById(dto.ownerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner", dto.ownerId()));
            playlist.setOwner(owner);
        }

        return playlistToDto(repository.save(playlist));
    }

    @Override
    @Transactional
    public PlaylistDTO update(String id, PlaylistDTO dto) {
        if (id == null || id.isBlank()) {
            throw new InvalidRequestException("Playlist ID cannot be null or blank");
        }
        
        Playlist playlist = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist", id));

        validatePlaylistDTO(dto);
        
        playlist.setName(dto.name());
        playlist.setDescription(dto.description());
        playlist.setHref(dto.href());
        if (dto.isPublic() != null) {
            playlist.setIsPublic(dto.isPublic());
        }

        // validate and set owner if provided
        if (dto.ownerId() != null && !dto.ownerId().isBlank()) {
            Listener owner = listenerRepository.findById(dto.ownerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner", dto.ownerId()));
            playlist.setOwner(owner);
        } else {
            // Allow clearing the owner by setting it to null
            playlist.setOwner(null);
        }

        return playlistToDto(repository.save(playlist));
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidRequestException("Playlist ID cannot be null or blank");
        }
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Playlist", id);
        }
        repository.deleteById(id);
    }
    
    private void validatePlaylistDTO(PlaylistDTO dto) {
        if (dto == null) {
            throw new InvalidRequestException("Playlist data cannot be null");
        }
        
        if (dto.playlistId() == null || dto.playlistId().isBlank()) {
            throw new InvalidRequestException("Playlist ID is required");
        }
        
        if (dto.name() == null || dto.name().isBlank()) {
            throw new InvalidRequestException("Playlist name is required");
        }
        
        // isPublic defaults to false if not provided, but validate if provided
        if (dto.isPublic() == null) {
            // This is okay, will default to false in create method
        }
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

