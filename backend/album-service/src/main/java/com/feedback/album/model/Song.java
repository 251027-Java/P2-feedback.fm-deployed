package com.feedback.album.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "song")
@NoArgsConstructor
public class Song {
    @Id
    @Column(name = "song_id", length = 64)
    private String songId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String href;

    @Column(name = "duration_ms", nullable = false)
    private Integer durationMs;

    @ManyToMany // defining the junction table to represent many to many
    @JoinTable(
        name = "artists_songs",
        joinColumns = @JoinColumn(name = "song_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id")    
    )
    private Set<Artist> artists = new HashSet<>();

    @ManyToMany(mappedBy = "songs")
    private Set<Playlist> playlists = new HashSet<>();

    // kenneth added this: many:many for albums and songs
    @ManyToMany
    @JoinTable(
        name = "albums_songs",
        joinColumns = @JoinColumn(name = "song_id"),
        inverseJoinColumns = @JoinColumn(name = "album_id")
    )
    private Set<Album> albums = new HashSet<>();

    public Song(String songId, String name, Integer durationMs, String href) {
        this.songId = songId;
        this.name = name;
        this.durationMs = durationMs;
        this.href = href;
    }

    @Override
    public String toString() {
        return "Songs{" +
                "songId='" + songId + '\'' +
                ", name='" + name + '\'' +
                ", durationMs=" + durationMs +
                '}';
    }
}