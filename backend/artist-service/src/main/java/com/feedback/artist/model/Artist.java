package com.feedback.artist.model;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // Tells JPA this is a database entity
@Data
@Table(name = "artist") // Maps to a database table
@NoArgsConstructor
public class Artist {

    @Id // marks the artistId as the primary key
    @Column(name = "artist_id", length = 64) // Maps to artist_id column
    private String artistId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String href;

    @ManyToMany(mappedBy = "artists")
    private Set<Song> songs = new HashSet<>();

    // kenneth added this: one:many for albums and artists
    @OneToMany(mappedBy = "artist")
    private Set<Album> albums = new HashSet<>();

    public Artist(String artistId, String name, String href) {
        this.artistId = artistId;
        this.name = name;
        this.href = href;
    }

    @Override
    public String toString(){
        return "Artist{" +
            "artistId='" + artistId + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}



