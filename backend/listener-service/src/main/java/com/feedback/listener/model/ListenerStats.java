package com.feedback.listener.model;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "listener_stats")
@Data
public class ListenerStats {
    @Id
    private String listenerId;

    private Long totalListeningTimeMs;
    private Integer totalSongsPlayed;
    private Integer currentStreak;
}
