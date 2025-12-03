package feedbackmodel;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;





@Entity
@Table(name = "songs")
public class Song {
    @Id
    @Column(name = "song_id", length = 64)
    private String songId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String href;

    @ManyToMany(mappedBy = "song")
    private Set<Artist> artists = new HashSet<>();

    public Song() {}
    public Song(String songId, String name, String href) {
        this.songId = songId;
        this.name = name;
        this.href = href;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Set<Artist> getArtists() {
        return artists;
    }

    public void setArtists(Set<Artist> artists) {
        this.artists = artists;
    }

    @Override
    public String toString() {
        return "Songs{" +
                "songId='" + songId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}