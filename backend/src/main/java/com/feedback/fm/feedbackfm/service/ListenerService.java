package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.ListenerDTO;
import com.feedback.fm.feedbackfm.model.Listener;
import com.feedback.fm.feedbackfm.repository.ListenerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ListenerService {

    private final ListenerRepository repository;

    public ListenerService(ListenerRepository repository) {
        this.repository = repository;
    }

    public List<ListenerDTO> getAllListeners() {
        return repository.findAll().stream()
                .map(this::listenerToDto)
                .toList();
    }

    public ListenerDTO getById(String id) {
        Optional<Listener> result = repository.findById(id);
        return result.map(this::listenerToDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listener not found"));
    }

    public List<ListenerDTO> findByDisplayName(String displayName) {
        return repository.findByDisplayName(displayName).stream()
                .map(this::listenerToDto)
                .toList();
    }

    public List<ListenerDTO> searchByDisplayName(String namePart) {
        return repository.findByDisplayNameContainingIgnoreCase(namePart).stream()
                .map(this::listenerToDto)
                .toList();
    }

    public ListenerDTO findByEmail(String email) {
        Listener listener = repository.findByEmail(email);
        if (listener == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Listener not found");
        }
        return listenerToDto(listener);
    }

    public ListenerDTO create(ListenerDTO dto) {
        Listener listener = new Listener(
                dto.listenerId(),
                dto.displayName(),
                dto.email(),
                dto.country(),
                dto.href()
        );
        return listenerToDto(repository.save(listener));
    }

    public ListenerDTO update(String id, ListenerDTO dto) {
        Listener listener = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listener not found"));

        listener.setDisplayName(dto.displayName());
        listener.setEmail(dto.email());
        listener.setCountry(dto.country());
        listener.setHref(dto.href());

        return listenerToDto(repository.save(listener));
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Listener not found");
        }
        repository.deleteById(id);
    }

    private ListenerDTO listenerToDto(Listener listener) {
        return new ListenerDTO(
                listener.getListenerId(),
                listener.getDisplayName(),
                listener.getEmail(),
                listener.getCountry(),
                listener.getHref()
        );
    }
}

