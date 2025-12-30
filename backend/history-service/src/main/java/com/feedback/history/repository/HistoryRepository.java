package com.feedback.history.repository;

import com.feedback.history.model.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

// kenneth: wrote this so that it matched up with Omar's model

public interface HistoryRepository extends JpaRepository<History, Long> {
    // find history records by listener ID
    List<History> findByListener_ListenerId(String listenerId);

    // find history records by song ID
    List<History> findBySong_SongId(String songId);

    // find history records by listener and song
    List<History> findByListener_ListenerIdAndSong_SongId(String listenerId, String songId);

    // find history records within a date range
    List<History> findByPlayedAtBetween(LocalDateTime start, LocalDateTime end);

    // find history records by listener within a date range
    List<History> findByListener_ListenerIdAndPlayedAtBetween(String listenerId, LocalDateTime start, LocalDateTime end);

    // find recent history records for a listener, ordered by playedAt descending
    List<History> findByListener_ListenerIdOrderByPlayedAtDesc(String listenerId);
}
