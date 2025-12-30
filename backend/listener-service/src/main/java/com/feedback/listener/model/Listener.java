package com.feedback.listener.model;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "listeners")
@Data
public class Listener {
    @Id
    @Column(name = "listener_id", length = 128)
    private String listenerId; // Spotify ID or generated

    private String displayName;
    private String email;
    private String country;
    private String href;

    private Long totalListeningTimeMs;
    private Integer totalSongsPlayed;
}
