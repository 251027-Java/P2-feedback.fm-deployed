package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.SongDTO;
import com.feedback.fm.feedbackfm.model.Song;
import com.feedback.fm.feedbackfm.repository.SongRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SongServiceImpl implements SongService {

    private final SongRepository repository;

    public SongServiceImpl(SongRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<SongDTO> getAllSongs() {
        return repository.findAll().stream()
                .map(this::songToDto)
                .toList();
    }

    @Override
    public Optional<SongDTO> getById(String id) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Song ID cannot be null or blank");
        }
        return repository.findById(id)
                .map(this::songToDto);
    }

    @Override
    public List<SongDTO> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return repository.findByName(name).stream()
                .map(this::songToDto)
                .toList();
    }

    @Override
    public List<SongDTO> searchByName(String namePart) {
        if (namePart == null || namePart.isBlank()) {
            return List.of();
        }
        return repository.findByNameContainingIgnoreCase(namePart).stream()
                .map(this::songToDto)
                .toList();
    }

    @Override
    public List<SongDTO> findByArtistName(String artistName) {
        if (artistName == null || artistName.isBlank()) {
            return List.of();
        }
        return repository.findByArtists_Name(artistName).stream()
                .map(this::songToDto)
                .toList();
    }

    @Override
    public List<SongDTO> findByDuration(Integer duration) {
        if (duration == null || duration <= 0) {
            return List.of();
        }
        return repository.findByDurationMs(duration).stream()
                .map(this::songToDto)
                .toList();
    }

    @Override
    public List<SongDTO> findByDurationRange(Integer minDuration, Integer maxDuration) {
        if (minDuration == null || maxDuration == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Min and max duration are required");
        }
        if (minDuration < 0 || maxDuration < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Duration must be positive");
        }
        if (minDuration > maxDuration) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Min duration must be less than or equal to max duration");
        }
        return repository.findByDurationMsBetween(minDuration, maxDuration).stream()
                .map(this::songToDto)
                .toList();
    }

    @Override
    @Transactional
    public SongDTO create(SongDTO dto) {
        validateSongDTO(dto);
        
        // check existing song
        if (repository.existsById(dto.songId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Song with ID '" + dto.songId() + "' already exists");
        }
        
        Song song = new Song(
                dto.songId(),
                dto.name(),
                dto.durationMs(),
                dto.href()
        );
        return songToDto(repository.save(song));
    }

    @Override
    @Transactional
    public SongDTO update(String id, SongDTO dto) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Song ID cannot be null or blank");
        }
        
        Song song = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Song not found with id: " + id));

        validateSongDTO(dto);
        
        song.setName(dto.name());
        song.setDurationMs(dto.durationMs());
        song.setHref(dto.href());

        return songToDto(repository.save(song));
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Song ID cannot be null or blank");
        }
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Song not found with id: " + id);
        }
        repository.deleteById(id);
    }
    
    private void validateSongDTO(SongDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Song data cannot be null");
        }
        
        if (dto.songId() == null || dto.songId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Song ID is required");
        }
        
        if (dto.name() == null || dto.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Song name is required");
        }
        
        if (dto.durationMs() == null || dto.durationMs() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Song duration must be positive");
        }
        
        // Validate reasonable duration (e.g., not more than 24 hours)
        if (dto.durationMs() > 86400000) { // 24 hours in milliseconds
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Song duration cannot exceed 24 hours");
        }
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

