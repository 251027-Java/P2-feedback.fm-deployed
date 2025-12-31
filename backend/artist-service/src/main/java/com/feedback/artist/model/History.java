package com.feedback.artist.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "history")
@NoArgsConstructor
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt;

    // kenneth changed this to be joinColumn instead of joinTable and removed the import
    @ManyToOne(fetch = FetchType.LAZY)// one history has one listener (listener who played the song)
    @JoinColumn(name = "listener_id")
    private Listener listener;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    public History(LocalDateTime playedAt, Listener listener, Song song) {
        this.playedAt = playedAt;
        this.listener = listener;
        this.song = song;
    }


    @PrePersist
    protected void onCreate() {
        if (playedAt == null) {
            playedAt = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "History{" +
                "historyId=" + historyId +
                ", playedAt=" + playedAt +
                ", listener=" + (listener != null ? listener.getListenerId() : "null") +
                ", song=" + (song != null ? song.getSongId() : "null") +
                '}';
    }

}
