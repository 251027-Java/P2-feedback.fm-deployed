package com.feedback.playlist.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

// kenneth wrote this out to match up with the M:M relationship between albums and songs and for Juan's album repo

@Entity
@Data
@Table(name = "album")
@NoArgsConstructor
public class Album {

    @Id
    @Column(name = "album_id", length = 64)
    private String albumId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(columnDefinition = "TEXT")
    private String href;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ManyToMany(mappedBy = "albums")
    private Set<Song> songs = new HashSet<>();

    public Album(String albumId, String title, Integer releaseYear, String href) {
        this.albumId = albumId;
        this.title = title;
        this.releaseYear = releaseYear;
        this.href = href;
    }

    @Override
    public String toString() {
        return "Album{" +
                "albumId='" + albumId + '\'' +
                ", title='" + title + '\'' +
                ", releaseYear=" + releaseYear +
                '}';
    }
}
