package FeedBackModel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import feedbackmodel.Listener;

@Service
public class listenerService {

    // Fields
    private final listenerRepository repository;

    // Constructor
    public listenerService(listenerRepository repository) {
        this.repository = repository;
    }

    // Methods

    public List<Listener> getAll() {
        return repository.findAll();
    }

    public Listener getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listener not found"));
    }

    public Listener getByEmail(String email) {
        Optional<Listener> listener = repository.findByEmail(email);
        return listener.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listener not found: " + email));
    }

    public Listener create(String listenerId, String displayName, String email, String country, String href) {
        // Check if listener already exists
        Optional<Listener> existingListener = repository.findById(listenerId);
        if (existingListener.isPresent()) {
            return update(listenerId, displayName, email, country, href);
        }

        // If listener does not exist, create new one
        Listener listener = new Listener(listenerId, displayName, email, country, href);
        return repository.save(listener);
    }

    public Listener update(String id, String displayName, String email, String country, String href) {
        Listener listener = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listener not found"));

        if (displayName != null) {
            listener.setDisplayName(displayName);
        }
        if (email != null) {
            listener.setEmail(email);
        }
        if (country != null) {
            listener.setCountry(country);
        }
        if (href != null) {
            listener.setHref(href);
        }

        return repository.save(listener);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    // Additional helper methods

    public boolean exists(String listenerId) {
        return repository.findById(listenerId).isPresent();
    }

    public boolean existsByEmail(String email) {
        return repository.findByEmail(email).isPresent();
    }

    public Listener getOrCreate(String listenerId, String displayName, String email, String country, String href) {
        return repository.findById(listenerId)
                .map(l -> update(listenerId, displayName, email, country, href))
                .orElseGet(() -> create(listenerId, displayName, email, country, href));
    }
}
