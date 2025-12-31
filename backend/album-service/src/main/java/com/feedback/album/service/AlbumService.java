package com.feedback.album.service;

import com.feedback.album.dtos.AlbumDTO;
import com.feedback.album.exception.InvalidRequestException;
import com.feedback.album.exception.ResourceNotFoundException;
import com.feedback.album.model.Album;
import com.feedback.album.model.Artist;
import com.feedback.album.repository.AlbumRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.feedback.album.service.ArtistService;
import com.feedback.album.dtos.ArtistDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AlbumService {

    private final AlbumRepository repository;
    private final ArtistService artistService;

    public AlbumService(AlbumRepository repository, ArtistService artistService) {
        this.repository = repository;
        this.artistService = artistService;
    }

    private Artist DTOToArtist(ArtistDTO dto) {
        return new Artist(
                dto.artistId(),
                dto.name(),
                dto.href()
        );
    }

    
    public List<AlbumDTO> getAllAlbums() {
        return repository.findAll().stream()
                .map(this::albumToDto)
                .toList();
    }

    
    public Optional<AlbumDTO> getById(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidRequestException("Album ID cannot be null or blank");
        }
        return repository.findById(id)
                .map(this::albumToDto);
    }

    
    public List<AlbumDTO> searchByTitle(String titlePart) {
        if (titlePart == null || titlePart.isBlank()) {
            return List.of();
        }
        return repository.findByTitleContainingIgnoreCase(titlePart).stream()
                .map(this::albumToDto)
                .toList();
    }

    
    public List<AlbumDTO> findByReleaseYear(Integer releaseYear) {
        if (releaseYear == null) {
            return List.of();
        }
        // Validate reasonable year range
        if (releaseYear < 1900 || releaseYear > 2100) {
            throw new InvalidRequestException("Release year must be between 1900 and 2100");
        }
        return repository.findByReleaseYear(releaseYear).stream()
                .map(this::albumToDto)
                .toList();
    }

    
    public List<AlbumDTO> findByArtistId(String artistId) {
        if (artistId == null || artistId.isBlank()) {
            return List.of();
        }
        // Verify artist exists
        if (!artistService.existsById(artistId)) {
            throw new ResourceNotFoundException("Artist", artistId);
        }
        return repository.findAll().stream()
                .filter(album -> album.getArtist() != null 
                    && album.getArtist().getArtistId().equals(artistId))
                .map(this::albumToDto)
                .toList();
    }

    
    @Transactional
    public AlbumDTO create(AlbumDTO dto) {
        validateAlbumDTO(dto);
        
        // check existing album
        if (repository.existsById(dto.albumId())) {
            throw new InvalidRequestException("Album with ID '" + dto.albumId() + "' already exists");
        }
        
        Album album = new Album(
                dto.albumId(),
                dto.title(),
                dto.releaseYear(),
                dto.href()
        );

        // validate and set artist if provided
        if (dto.artistId() != null && !dto.artistId().isBlank()) {
            ArtistDTO artist = artistService.findById(dto.artistId());
            if (artist == null) {
                throw new ResourceNotFoundException("Artist", dto.artistId());
            }

            album.setArtist(DTOToArtist(artist));
        }

        return albumToDto(repository.save(album));
    }

    
    @Transactional
    public AlbumDTO update(String id, AlbumDTO dto) {
        if (id == null || id.isBlank()) {
            throw new InvalidRequestException("Album ID cannot be null or blank");
        }
        
        Album album = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album", id));

        validateAlbumDTO(dto);
        
        album.setTitle(dto.title());
        album.setReleaseYear(dto.releaseYear());
        album.setHref(dto.href());

        // validate and set artist if provided
        if (dto.artistId() != null && !dto.artistId().isBlank()) {
            ArtistDTO artist = artistService.findById(dto.artistId());
            if (artist == null) {
                throw new ResourceNotFoundException("Artist", dto.artistId());
            }
            album.setArtist(DTOToArtist(artist));
        } else {
            // Allow clearing the artist by setting it to null
            album.setArtist(null);
        }

        return albumToDto(repository.save(album));
    }

    
    @Transactional
    public void delete(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidRequestException("Album ID cannot be null or blank");
        }
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Album", id);
        }
        repository.deleteById(id);
    }
    
    private void validateAlbumDTO(AlbumDTO dto) {
        if (dto == null) {
            throw new InvalidRequestException("Album data cannot be null");
        }
        
        if (dto.albumId() == null || dto.albumId().isBlank()) {
            throw new InvalidRequestException("Album ID is required");
        }
        
        if (dto.title() == null || dto.title().isBlank()) {
            throw new InvalidRequestException("Album title is required");
        }
        
        // validate release year if provided
        if (dto.releaseYear() != null) {
            if (dto.releaseYear() < 1900 || dto.releaseYear() > 2100) {
                throw new InvalidRequestException("Release year must be between 1900 and 2100");
            }
        }
    }

    private AlbumDTO albumToDto(Album album) {
        String artistId = album.getArtist() != null ? album.getArtist().getArtistId() : null;

        List<String> songIds = album.getSongs().stream()
                .map(song -> song.getSongId())
                .collect(Collectors.toList());

        return new AlbumDTO(
                album.getAlbumId(),
                album.getTitle(),
                album.getReleaseYear(),
                album.getHref(),
                artistId,
                songIds
        );
    }
}

