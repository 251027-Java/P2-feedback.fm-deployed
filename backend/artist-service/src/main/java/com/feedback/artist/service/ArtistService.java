package com.feedback.artist.service;

import com.feedback.artist.dtos.ArtistDTO;
import com.feedback.artist.exception.InvalidRequestException;
import com.feedback.artist.exception.ResourceNotFoundException;
import com.feedback.artist.model.Artist;
import com.feedback.artist.repository.ArtistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository repository;

    public ArtistService(ArtistRepository repository) {
        this.repository = repository;
    }

    
    public List<ArtistDTO> getAllArtists() {
        return repository.findAll().stream()
                .map(this::artistToDto)
                .toList();
    }

    
    public Optional<ArtistDTO> getById(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidRequestException("Artist ID cannot be null or blank");
        }
        return repository.findById(id)
                .map(this::artistToDto);
    }

    public boolean existsById(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidRequestException("Artist ID cannot be null or blank");
        }
        return repository.existsById(id);
    }

    
    public List<ArtistDTO> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return repository.findByName(name).stream()
                .map(this::artistToDto)
                .toList();
    }

    
    public List<ArtistDTO> searchByName(String namePart) {
        if (namePart == null || namePart.isBlank()) {
            return List.of();
        }
        return repository.findByNameContainingIgnoreCase(namePart).stream()
                .map(this::artistToDto)
                .toList();
    }

    
    @Transactional
    public ArtistDTO create(ArtistDTO dto) {
        validateArtistDTO(dto);
        
        // check existing artist
        if (repository.existsById(dto.artistId())) {
            throw new InvalidRequestException("Artist with ID '" + dto.artistId() + "' already exists");
        }
        
        Artist artist = new Artist(
                dto.artistId(),
                dto.name(),
                dto.href()
        );
        return artistToDto(repository.save(artist));
    }

    
    @Transactional
    public ArtistDTO update(String id, ArtistDTO dto) {
        if (id == null || id.isBlank()) {
            throw new InvalidRequestException("Artist ID cannot be null or blank");
        }
        
        Artist artist = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", id));

        validateArtistDTO(dto);
        
        artist.setName(dto.name());
        artist.setHref(dto.href());

        return artistToDto(repository.save(artist));
    }

    
    @Transactional
    public void delete(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidRequestException("Artist ID cannot be null or blank");
        }
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Artist", id);
        }
        repository.deleteById(id);
    }
    
    private void validateArtistDTO(ArtistDTO dto) {
        if (dto == null) {
            throw new InvalidRequestException("Artist data cannot be null");
        }
        
        if (dto.artistId() == null || dto.artistId().isBlank()) {
            throw new InvalidRequestException("Artist ID is required");
        }
        
        if (dto.name() == null || dto.name().isBlank()) {
            throw new InvalidRequestException("Artist name is required");
        }
    }

    private ArtistDTO artistToDto(Artist artist) {
        List<String> albumIds = artist.getAlbums().stream()
                .map(album -> album.getAlbumId())
                .collect(Collectors.toList());

        return new ArtistDTO(
                artist.getArtistId(),
                artist.getName(),
                artist.getHref(),
                albumIds
        );
    }
}

