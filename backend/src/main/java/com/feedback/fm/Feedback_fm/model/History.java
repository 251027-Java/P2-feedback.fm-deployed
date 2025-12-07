package feedbackmodel;


import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;


@Entity
@Table(name = "history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt;

    @ManyToOne(fetch = FetchType.LAZY)// one history has one listener (listener who played the song)
    @JoinTable(name = "listener_id", joinColumns = @JoinColumn(name = "history_id"), inverseJoinColumns = @JoinColumn(name = "listener_id"))
    private Listener listener;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    public History(){}

    public History(LocalDateTime playedAt, Listener listener, Song song) {
        this.playedAt = playedAt;
        this.listener = listener;
        this.song = song;
    }


    @PrePersist
    protected void onCreate() {
        if (playedAt == null) {
            playedAt = LocalDateTime.now();
        }
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    @Override
    public String toString() {
        return "History{" +
                "historyId=" + historyId +
                ", playedAt=" + playedAt +
                ", listener=" + (listener != null ? listener.getListenerId() : "null") +
                ", song=" + (song != null ? song.getSongId() : "null") +
                '}';
    }

}
