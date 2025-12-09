package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.HistoryDTO;
import com.feedback.fm.feedbackfm.model.History;
import com.feedback.fm.feedbackfm.repository.HistoryRepository;
import com.feedback.fm.feedbackfm.repository.ListenerRepository;
import com.feedback.fm.feedbackfm.repository.SongRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HistoryService {

    private final HistoryRepository repository;
    private final ListenerRepository listenerRepository;
    private final SongRepository songRepository;

    public HistoryService(HistoryRepository repository, ListenerRepository listenerRepository, SongRepository songRepository) {
        this.repository = repository;
        this.listenerRepository = listenerRepository;
        this.songRepository = songRepository;
    }

    public List<HistoryDTO> getAllHistory() {
        return repository.findAll().stream()
                .map(this::historyToDto)
                .toList();
    }

    public HistoryDTO getById(Long id) {
        Optional<History> result = repository.findById(id);
        return result.map(this::historyToDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "History record not found"));
    }

    public List<HistoryDTO> findByListenerId(String listenerId) {
        return repository.findByListener_ListenerId(listenerId).stream()
                .map(this::historyToDto)
                .toList();
    }

    public List<HistoryDTO> findBySongId(String songId) {
        return repository.findBySong_SongId(songId).stream()
                .map(this::historyToDto)
                .toList();
    }

    public List<HistoryDTO> getRecentHistoryByListener(String listenerId) {
        return repository.findByListener_ListenerIdOrderByPlayedAtDesc(listenerId).stream()
                .map(this::historyToDto)
                .toList();
    }

    public List<HistoryDTO> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return repository.findByPlayedAtBetween(start, end).stream()
                .map(this::historyToDto)
                .toList();
    }

    public HistoryDTO create(HistoryDTO dto) {
        History history = new History();
        
        if (dto.playedAt() != null) {
            history.setPlayedAt(dto.playedAt());
        }

        if (dto.listenerId() != null) {
            listenerRepository.findById(dto.listenerId())
                    .ifPresentOrElse(
                            history::setListener,
                            () -> {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Listener not found");
                            }
                    );
        }

        if (dto.songId() != null) {
            songRepository.findById(dto.songId())
                    .ifPresentOrElse(
                            history::setSong,
                            () -> {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found");
                            }
                    );
        }

        return historyToDto(repository.save(history));
    }

    public HistoryDTO update(Long id, HistoryDTO dto) {
        History history = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "History record not found"));

        if (dto.playedAt() != null) {
            history.setPlayedAt(dto.playedAt());
        }

        if (dto.listenerId() != null) {
            listenerRepository.findById(dto.listenerId())
                    .ifPresentOrElse(
                            history::setListener,
                            () -> {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Listener not found");
                            }
                    );
        }

        if (dto.songId() != null) {
            songRepository.findById(dto.songId())
                    .ifPresentOrElse(
                            history::setSong,
                            () -> {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found");
                            }
                    );
        }

        return historyToDto(repository.save(history));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "History record not found");
        }
        repository.deleteById(id);
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

