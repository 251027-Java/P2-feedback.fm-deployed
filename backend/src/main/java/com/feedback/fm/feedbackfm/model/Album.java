package com.feedback.fm.feedbackfm.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

// kenneth wrote this out to match up with the M:M relationship between albums and songs and for Juan's album repo

@Entity
@Table(name = "album")
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

    // Default constructor required by JPA/Hibernate
    public Album() {
    }

    public Album(String albumId, String title, Integer releaseYear, String href) {
        this.albumId = albumId;
        this.title = title;
        this.releaseYear = releaseYear;
        this.href = href;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public void setSongs(Set<Song> songs) {
        this.songs = songs;
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
