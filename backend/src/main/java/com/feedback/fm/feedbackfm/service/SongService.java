package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.SongDTO;
import com.feedback.fm.feedbackfm.model.Song;
import com.feedback.fm.feedbackfm.repository.SongRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SongService {

    private final SongRepository repository;

    public SongService(SongRepository repository) {
        this.repository = repository;
    }

    public List<SongDTO> getAllSongs() {
        return repository.findAll().stream()
                .map(this::songToDto)
                .toList();
    }

    public SongDTO getById(String id) {
        Optional<Song> result = repository.findById(id);
        return result.map(this::songToDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found"));
    }

    public List<SongDTO> searchByName(String namePart) {
        return repository.findByNameContainingIgnoreCase(namePart).stream()
                .map(this::songToDto)
                .toList();
    }

    public List<SongDTO> findByArtistName(String artistName) {
        return repository.findByArtists_Name(artistName).stream()
                .map(this::songToDto)
                .toList();
    }

    public SongDTO create(SongDTO dto) {
        Song song = new Song(
                dto.songId(),
                dto.name(),
                dto.durationMs(),
                dto.href()
        );
        return songToDto(repository.save(song));
    }

    public SongDTO update(String id, SongDTO dto) {
        Song song = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found"));

        song.setName(dto.name());
        song.setDurationMs(dto.durationMs());
        song.setHref(dto.href());

        return songToDto(repository.save(song));
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found");
        }
        repository.deleteById(id);
    }

    private SongDTO songToDto(Song song) {
        List<String> artistIds = song.getArtists().stream()
                .map(artist -> artist.getArtistId())
                .collect(Collectors.toList());

        List<String> albumIds = song.getAlbums().stream()
                .map(album -> album.getAlbumId())
                .collect(Collectors.toList());

        return new SongDTO(
                song.getSongId(),
                song.getName(),
                song.getHref(),
                song.getDurationMs(),
                artistIds,
                albumIds
        );
    }
}

