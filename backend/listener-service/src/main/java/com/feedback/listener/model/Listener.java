package com.feedback.listener.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "listener")
@Data
@NoArgsConstructor
public class Listener {

    @Id
    @Column(name = "listener_id", length = 64)
    private String listenerId;

    @Column(name = "display_name", columnDefinition = "TEXT")
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String email;

    @Column(length = 10)
    private String country;
    
    @Column(columnDefinition = "TEXT")
    private String href;

    @Column(name = "total_listening_time_ms")
    private Long totalListeningTimeMs = 0L;

    @Column(name = "total_songs_played")
    private Integer totalSongsPlayed = 0;

    @OneToMany(mappedBy = "owner")
    private Set<Playlist> playlists = new HashSet<>(); 
    
    @OneToMany(mappedBy = "listener", cascade = CascadeType.ALL)
    private Set<History> history = new HashSet<>();

    public Listener(String listenerId, String displayName, String email, String country, String href) {
        this.listenerId = listenerId;
        this.displayName = displayName;
        this.email = email;
        this.country = country;
        this.href = href;
    }

    public Long getTotalListeningTimeMs() {
        return totalListeningTimeMs != null ? totalListeningTimeMs : 0L;
    }

    public void setTotalListeningTimeMs(Long totalListeningTimeMs) {
        this.totalListeningTimeMs = totalListeningTimeMs != null ? totalListeningTimeMs : 0L;
    }

    public Integer getTotalSongsPlayed() {
        return totalSongsPlayed != null ? totalSongsPlayed : 0;
    }

    public void setTotalSongsPlayed(Integer totalSongsPlayed) {
        this.totalSongsPlayed = totalSongsPlayed != null ? totalSongsPlayed : 0;
    }

    @Override
    public String toString() {
        return "Listener{" +
                "listenerId='" + listenerId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

}