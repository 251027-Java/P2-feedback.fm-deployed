package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.AlbumDTO;
import com.feedback.fm.feedbackfm.model.Album;
import com.feedback.fm.feedbackfm.model.Artist;
import com.feedback.fm.feedbackfm.repository.AlbumRepository;
import com.feedback.fm.feedbackfm.repository.ArtistRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository repository;
    private final ArtistRepository artistRepository;

    public AlbumServiceImpl(AlbumRepository repository, ArtistRepository artistRepository) {
        this.repository = repository;
        this.artistRepository = artistRepository;
    }

    @Override
    public List<AlbumDTO> getAllAlbums() {
        return repository.findAll().stream()
                .map(this::albumToDto)
                .toList();
    }

    @Override
    public Optional<AlbumDTO> getById(String id) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Album ID cannot be null or blank");
        }
        return repository.findById(id)
                .map(this::albumToDto);
    }

    @Override
    public List<AlbumDTO> searchByTitle(String titlePart) {
        if (titlePart == null || titlePart.isBlank()) {
            return List.of();
        }
        return repository.findByTitleContainingIgnoreCase(titlePart).stream()
                .map(this::albumToDto)
                .toList();
    }

    @Override
    public List<AlbumDTO> findByReleaseYear(Integer releaseYear) {
        if (releaseYear == null) {
            return List.of();
        }
        // Validate reasonable year range
        if (releaseYear < 1900 || releaseYear > 2100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Release year must be between 1900 and 2100");
        }
        return repository.findByReleaseYear(releaseYear).stream()
                .map(this::albumToDto)
                .toList();
    }

    @Override
    public List<AlbumDTO> findByArtistId(String artistId) {
        if (artistId == null || artistId.isBlank()) {
            return List.of();
        }
        // Verify artist exists
        if (!artistRepository.existsById(artistId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Artist not found with id: " + artistId);
        }
        return repository.findAll().stream()
                .filter(album -> album.getArtist() != null 
                    && album.getArtist().getArtistId().equals(artistId))
                .map(this::albumToDto)
                .toList();
    }

    @Override
    @Transactional
    public AlbumDTO create(AlbumDTO dto) {
        validateAlbumDTO(dto);
        
        // check existing album
        if (repository.existsById(dto.albumId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Album with ID '" + dto.albumId() + "' already exists");
        }
        
        Album album = new Album(
                dto.albumId(),
                dto.title(),
                dto.releaseYear(),
                dto.href()
        );

        // validate and set artist if provided
        if (dto.artistId() != null && !dto.artistId().isBlank()) {
            Artist artist = artistRepository.findById(dto.artistId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Artist not found with id: " + dto.artistId()));
            album.setArtist(artist);
        }

        return albumToDto(repository.save(album));
    }

    @Override
    @Transactional
    public AlbumDTO update(String id, AlbumDTO dto) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Album ID cannot be null or blank");
        }
        
        Album album = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Album not found with id: " + id));

        validateAlbumDTO(dto);
        
        album.setTitle(dto.title());
        album.setReleaseYear(dto.releaseYear());
        album.setHref(dto.href());

        // validate and set artist if provided
        if (dto.artistId() != null && !dto.artistId().isBlank()) {
            Artist artist = artistRepository.findById(dto.artistId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Artist not found with id: " + dto.artistId()));
            album.setArtist(artist);
        } else {
            // Allow clearing the artist by setting it to null
            album.setArtist(null);
        }

        return albumToDto(repository.save(album));
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Album ID cannot be null or blank");
        }
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Album not found with id: " + id);
        }
        repository.deleteById(id);
    }
    
    private void validateAlbumDTO(AlbumDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Album data cannot be null");
        }
        
        if (dto.albumId() == null || dto.albumId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Album ID is required");
        }
        
        if (dto.title() == null || dto.title().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Album title is required");
        }
        
        // validate release year if provided
        if (dto.releaseYear() != null) {
            if (dto.releaseYear() < 1900 || dto.releaseYear() > 2100) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Release year must be between 1900 and 2100");
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

