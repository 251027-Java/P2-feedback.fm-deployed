package com.feedback.song.model;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "songs")
@Data
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Integer durationMs;

    private Long albumId;

    @ElementCollection
    @CollectionTable(name = "song_artists", joinColumns = @JoinColumn(name = "song_id"))
    @Column(name = "artist_id")
    private Set<Long> artistIds = new HashSet<>();
}
