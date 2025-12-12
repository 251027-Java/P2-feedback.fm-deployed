package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.HistoryDTO;
import com.feedback.fm.feedbackfm.exception.InvalidRequestException;
import com.feedback.fm.feedbackfm.exception.ResourceNotFoundException;
import com.feedback.fm.feedbackfm.model.History;
import com.feedback.fm.feedbackfm.model.Listener;
import com.feedback.fm.feedbackfm.model.Song;
import com.feedback.fm.feedbackfm.repository.HistoryRepository;
import com.feedback.fm.feedbackfm.repository.ListenerRepository;
import com.feedback.fm.feedbackfm.repository.SongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository repository;
    private final ListenerRepository listenerRepository;
    private final SongRepository songRepository;

    public HistoryServiceImpl(HistoryRepository repository, ListenerRepository listenerRepository, SongRepository songRepository) {
        this.repository = repository;
        this.listenerRepository = listenerRepository;
        this.songRepository = songRepository;
    }

    @Override
    public List<HistoryDTO> getAllHistory() {
        return repository.findAll().stream()
                .map(this::historyToDto)
                .toList();
    }

    @Override
    public Optional<HistoryDTO> getById(Long id) {
        if (id == null) {
            throw new InvalidRequestException("History ID cannot be null");
        }
        return repository.findById(id)
                .map(this::historyToDto);
    }

    @Override
    public List<HistoryDTO> findByListenerId(String listenerId) {
        if (listenerId == null || listenerId.isBlank()) {
            return List.of();
        }
        return repository.findByListener_ListenerId(listenerId).stream()
                .map(this::historyToDto)
                .toList();
    }

    @Override
    public List<HistoryDTO> findBySongId(String songId) {
        if (songId == null || songId.isBlank()) {
            return List.of();
        }
        return repository.findBySong_SongId(songId).stream()
                .map(this::historyToDto)
                .toList();
    }

    @Override
    public List<HistoryDTO> findByListenerIdAndSongId(String listenerId, String songId) {
        if (listenerId == null || listenerId.isBlank() || songId == null || songId.isBlank()) {
            return List.of();
        }
        return repository.findByListener_ListenerIdAndSong_SongId(listenerId, songId).stream()
                .map(this::historyToDto)
                .toList();
    }

    @Override
    public List<HistoryDTO> getRecentHistoryByListener(String listenerId, int limit) {
        if (listenerId == null || listenerId.isBlank()) {
            return List.of();
        }
        if (limit <= 0) {
            limit = 20; // Default limit
        }
        if (limit > 100) {
            limit = 100; // Max limit to prevent performance issues
        }
        
        List<History> allHistory = repository.findByListener_ListenerIdOrderByPlayedAtDesc(listenerId);
        return allHistory.stream()
                .limit(limit)
                .map(this::historyToDto)
                .toList();
    }

    @Override
    public List<HistoryDTO> findByDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new InvalidRequestException("Start and end dates are required");
        }
        if (start.isAfter(end)) {
            throw new InvalidRequestException("Start date must be before end date");
        }
        return repository.findByPlayedAtBetween(start, end).stream()
                .map(this::historyToDto)
                .toList();
    }

    @Override
    public List<HistoryDTO> findByListenerIdAndDateRange(String listenerId, LocalDateTime start, LocalDateTime end) {
        if (listenerId == null || listenerId.isBlank()) {
            return List.of();
        }
        if (start == null || end == null) {
            throw new InvalidRequestException("Start and end dates are required");
        }
        if (start.isAfter(end)) {
            throw new InvalidRequestException("Start date must be before end date");
        }
        return repository.findByListener_ListenerIdAndPlayedAtBetween(listenerId, start, end).stream()
                .map(this::historyToDto)
                .toList();
    }

    @Override
    @Transactional
    public HistoryDTO create(HistoryDTO dto) {
        validateHistoryDTO(dto);
        
        History history = new History();
        
        // Set playedAt - if not provided, will be set by @PrePersist
        if (dto.playedAt() != null) {
            history.setPlayedAt(dto.playedAt());
        } else {
            history.setPlayedAt(LocalDateTime.now());
        }

        // Validate and set listener
        if (dto.listenerId() == null || dto.listenerId().isBlank()) {
            throw new InvalidRequestException("Listener ID is required");
        }
        Listener listener = listenerRepository.findById(dto.listenerId())
                .orElseThrow(() -> new ResourceNotFoundException("Listener", dto.listenerId()));
        history.setListener(listener);

        // Validate and set song (required)
        if (dto.songId() == null || dto.songId().isBlank()) {
            throw new InvalidRequestException("Song ID is required");
        }
        Song song = songRepository.findById(dto.songId())
                .orElseThrow(() -> new ResourceNotFoundException("Song", dto.songId()));
        history.setSong(song);

        return historyToDto(repository.save(history));
    }

    @Override
    @Transactional
    public HistoryDTO update(Long id, HistoryDTO dto) {
        if (id == null) {
            throw new InvalidRequestException("History ID cannot be null");
        }
        
        History history = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("History", String.valueOf(id)));

        // Validate DTO (but allow partial updates)
        if (dto == null) {
            throw new InvalidRequestException("History data cannot be null");
        }

        // Update playedAt if provided
        if (dto.playedAt() != null) {
            history.setPlayedAt(dto.playedAt());
        }

        // Update listener if provided
        if (dto.listenerId() != null && !dto.listenerId().isBlank()) {
            Listener listener = listenerRepository.findById(dto.listenerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Listener", dto.listenerId()));
            history.setListener(listener);
        }

        // Update song if provided (required field, so must be valid)
        if (dto.songId() != null && !dto.songId().isBlank()) {
            Song song = songRepository.findById(dto.songId())
                    .orElseThrow(() -> new ResourceNotFoundException("Song", dto.songId()));
            history.setSong(song);
        }

        return historyToDto(repository.save(history));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new InvalidRequestException("History ID cannot be null");
        }
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("History", String.valueOf(id));
        }
        repository.deleteById(id);
    }
    
    private void validateHistoryDTO(HistoryDTO dto) {
        if (dto == null) {
            throw new InvalidRequestException("History data cannot be null");
        }
        
        // Listener ID is required for creation
        if (dto.listenerId() == null || dto.listenerId().isBlank()) {
            throw new InvalidRequestException("Listener ID is required");
        }
        
        // Song ID is required (song is nullable = false in model)
        if (dto.songId() == null || dto.songId().isBlank()) {
            throw new InvalidRequestException("Song ID is required");
        }
        
        // Validate playedAt is not in the future (reasonable business rule)
        if (dto.playedAt() != null && dto.playedAt().isAfter(LocalDateTime.now())) {
            throw new InvalidRequestException("Played at date cannot be in the future");
        }
    }

    private HistoryDTO historyToDto(History history) {
        String listenerId = history.getListener() != null ? history.getListener().getListenerId() : null;
        String songId = history.getSong() != null ? history.getSong().getSongId() : null;

        return new HistoryDTO(
                history.getHistoryId(),
                history.getPlayedAt(),
                listenerId,
                songId
        );
    }
}

