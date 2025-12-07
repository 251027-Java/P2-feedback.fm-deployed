package FeedBackModel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import FeedBackModel.Artist;

@Service
public class artistService {

    // Fields
    private final artistRepository repository;

    // Constructor
    public artistService(artistRepository repository) {
        this.repository = repository;
    }

    // Methods

    public List<Artist> getAll() {
        return repository.findAll();
    }

    public Artist getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found"));
    }

    public Artist getByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found: " + name));
    }

    public Artist create(String artistId, String name, String href) {
        // Check if artist already exists
        Optional<Artist> existingArtist = repository.findById(artistId);
        if (existingArtist.isPresent()) {
            return update(artistId, name, href);
        }

        // If artist does not exist, create new one
        Artist artist = new Artist(artistId, name, href);
        return repository.save(artist);
    }

    public Artist update(String id, String name, String href) {
        Artist artist = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found"));

        if (name != null) {
            artist.setName(name);
        }
        if (href != null) {
            artist.setHref(href);
        }

        return repository.save(artist);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    // Additional helper methods

    public boolean exists(String artistId) {
        return repository.findById(artistId).isPresent();
    }

    public boolean existsByName(String name) {
        return repository.findByName(name).isPresent();
    }

    public Artist getOrCreate(String artistId, String name, String href) {
        return repository.findById(artistId)
                .map(a -> update(artistId, name, href))
                .orElseGet(() -> create(artistId, name, href));
    }
}
