package com.feedback.listener.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table (name = "playlists")
@NoArgsConstructor
public class Playlist { //Playlist Columns: playlist_id, name, owner_id, is_public, href 
    @Id
    @Column(name = "playlist_id", length = 64)
    private String playlistId;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;
            
    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    // kenneth: i don't think we need this column but it was in Juan's repo so i added this in case
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String href;

    @ManyToOne(fetch = FetchType.LAZY)// one playlist has one owner (listener)
    @JoinColumn(name = "owner_id")
    private Listener owner;

    @ManyToMany
    @JoinTable(
        name = "playlists_songs",
        joinColumns = @JoinColumn(name = "playlist_id"),
        inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private List<Song> songs = new ArrayList<>();

    public Playlist(String playlistId, String name, String href, Boolean isPublic, Listener owner) {
        this.playlistId = playlistId;
        this.isPublic = isPublic;
        this.owner = owner;
        this.name = name;
        this.href = href;
    }

}