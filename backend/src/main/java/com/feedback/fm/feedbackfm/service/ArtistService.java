package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.ArtistDTO;
import com.feedback.fm.feedbackfm.model.Artist;
import com.feedback.fm.feedbackfm.repository.ArtistRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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

    public ArtistDTO getById(String id) {
        Optional<Artist> result = repository.findById(id);
        return result.map(this::artistToDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found"));
    }

    public List<ArtistDTO> searchByName(String namePart) {
        return repository.findByNameContainingIgnoreCase(namePart).stream()
                .map(this::artistToDto)
                .toList();
    }

    public ArtistDTO create(ArtistDTO dto) {
        Artist artist = new Artist(
                dto.artistId(),
                dto.name(),
                dto.href()
        );
        return artistToDto(repository.save(artist));
    }

    public ArtistDTO update(String id, ArtistDTO dto) {
        Artist artist = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found"));

        artist.setName(dto.name());
        artist.setHref(dto.href());

        return artistToDto(repository.save(artist));
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found");
        }
        repository.deleteById(id);
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

