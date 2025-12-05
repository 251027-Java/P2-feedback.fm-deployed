package feedbackmodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table (name = "playlists")
    public class Playlist { //Playlist Columns: playlist_id, name, owner_id, is_public, href 
        @Id
        @Column(name = "playlist_id", length = 64)
        private String playlistId;

        @Column(name = "is_public", nullable = false)
        private Boolean isPublic;
                
        @Column(nullable = false, columnDefinition = "TEXT")
        private String name;

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

        public Playlist() {}
        public Playlist(String playlistId, String name, String href) {
            this.playlistId = playlistId;
            this.isPublic = isPublic;
            this.owner = owner;
            this.name = name;
            this.href = href;
        }


        public List<Song> getSongs() {
            return songs;
        }
        public void setSongs(List<Song> songs) {
            this.songs = songs;
        }


        public String getPlaylistId() {
            return playlistId;
        }
        public void setPlaylistId(String playlistId) {
            this.playlistId = playlistId;
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

        public Listener getOwner() {
            return owner;
        }
        public void setOwner(Listener owner) {
            this.owner = owner;
        }
        public Boolean getIsPublic() {
            return isPublic;
        }
        public void setIsPublic(Boolean isPublic) {
            this.isPublic = isPublic;
        }

    }