package FeedBackModel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import FeedBackModel.Song;

@Service
public class songService {

    // Fields
    private final songRepository repository;

    // Constructor
    public songService(songRepository repository) {
        this.repository = repository;
    }

    // Methods

    public List<Song> getAll() {
        return repository.findAll();
    }

    public Song getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found"));
    }

    public List<Song> getByName(String name) {
        return repository.findByName(name);
    }

    public Song create(String songId, String name, Integer durationMs, String href) {
        // Check if song already exists
        Optional<Song> existingSong = repository.findById(songId);
        if (existingSong.isPresent()) {
            return update(songId, name, durationMs, href);
        }

        // If song does not exist, create new one
        Song song = new Song(songId, name, durationMs, href);
        return repository.save(song);
    }

    public Song update(String id, String name, Integer durationMs, String href) {
        Song song = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found"));

        if (name != null) {
            song.setName(name);
        }
        if (durationMs != null) {
            song.setDurationMs(durationMs);
        }
        if (href != null) {
            song.setHref(href);
        }

        return repository.save(song);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    // Additional helper methods

    public boolean exists(String songId) {
        return repository.findById(songId).isPresent();
    }

    public Song getOrCreate(String songId, String name, Integer durationMs, String href) {
        return repository.findById(songId)
                .map(s -> update(songId, name, durationMs, href))
                .orElseGet(() -> create(songId, name, durationMs, href));
    }
}
