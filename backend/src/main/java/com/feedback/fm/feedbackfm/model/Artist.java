package com.feedback.fm.feedbackfm.model;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity // Tells JPA this is a database entity
@Table(name = "artist") // Maps to a database table
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

    // Default constructor required by JPA/Hibernate
    public Artist() {
    }

    public Artist(String artistId, String name, String href) {
        this.artistId = artistId;
        this.name = name;
        this.href = href;
    }

    public String getArtistId(){
        return artistId;
    }

    public void setArtistId(String artistId){
        this.artistId = artistId;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getHref(){
        return href;
    }

    public void setHref(String href){
        this.href = href;
    }   

    public Set<Song> getSongs(){
        return songs;
    }

    public void setSongs(Set<Song> songs){
        this.songs = songs;
    }

    // kenneth: added this so the new album model can be added to the artist model
    public Set<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(Set<Album> albums) {
        this.albums = albums;
    }

    @Override
    public String toString(){
        return "Artist{" +
            "artistId='" + artistId + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}



