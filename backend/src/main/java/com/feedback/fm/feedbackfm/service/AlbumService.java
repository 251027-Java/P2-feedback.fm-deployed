package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.AlbumDTO;
import com.feedback.fm.feedbackfm.model.Album;
import com.feedback.fm.feedbackfm.repository.AlbumRepository;
import com.feedback.fm.feedbackfm.repository.ArtistRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final AlbumRepository repository;
    private final ArtistRepository artistRepository;

    public AlbumService(AlbumRepository repository, ArtistRepository artistRepository) {
        this.repository = repository;
        this.artistRepository = artistRepository;
    }

    public List<AlbumDTO> getAllAlbums() {
        return repository.findAll().stream()
                .map(this::albumToDto)
                .toList();
    }

    public AlbumDTO getById(String id) {
        Optional<Album> result = repository.findById(id);
        return result.map(this::albumToDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found"));
    }

    public List<AlbumDTO> searchByTitle(String titlePart) {
        return repository.findByTitleContainingIgnoreCase(titlePart).stream()
                .map(this::albumToDto)
                .toList();
    }

    public List<AlbumDTO> findByReleaseYear(Integer releaseYear) {
        return repository.findByReleaseYear(releaseYear).stream()
                .map(this::albumToDto)
                .toList();
    }

    public AlbumDTO create(AlbumDTO dto) {
        Album album = new Album(
                dto.albumId(),
                dto.title(),
                dto.releaseYear(),
                dto.href()
        );

        if (dto.artistId() != null) {
            artistRepository.findById(dto.artistId())
                    .ifPresent(album::setArtist);
        }

        return albumToDto(repository.save(album));
    }

    public AlbumDTO update(String id, AlbumDTO dto) {
        Album album = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found"));

        album.setTitle(dto.title());
        album.setReleaseYear(dto.releaseYear());
        album.setHref(dto.href());

        if (dto.artistId() != null) {
            artistRepository.findById(dto.artistId())
                    .ifPresent(album::setArtist);
        }

        return albumToDto(repository.save(album));
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found");
        }
        repository.deleteById(id);
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

