package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.ListenerDTO;
import com.feedback.fm.feedbackfm.exception.InvalidRequestException;
import com.feedback.fm.feedbackfm.exception.ResourceNotFoundException;
import com.feedback.fm.feedbackfm.model.Listener;
import com.feedback.fm.feedbackfm.repository.ListenerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ListenerServiceImpl implements ListenerService {

    private final ListenerRepository repository;

    public ListenerServiceImpl(ListenerRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ListenerDTO> getAllListeners() {
        return repository.findAll().stream()
                .map(this::listenerToDto)
                .toList();
    }

    @Override
    public Optional<ListenerDTO> getById(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidRequestException("Listener ID cannot be null or blank");
        }
        return repository.findById(id)
                .map(this::listenerToDto);
    }

    @Override
    public List<ListenerDTO> findByDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            return List.of();
        }
        return repository.findByDisplayName(displayName).stream()
                .map(this::listenerToDto)
                .toList();
    }

    @Override
    public List<ListenerDTO> searchByDisplayName(String namePart) {
        if (namePart == null || namePart.isBlank()) {
            return List.of();
        }
        return repository.findByDisplayNameContainingIgnoreCase(namePart).stream()
                .map(this::listenerToDto)
                .toList();
    }

    @Override
    public Optional<ListenerDTO> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidRequestException("Email cannot be null or blank");
        }
        Listener listener = repository.findByEmail(email);
        return listener != null ? Optional.of(listenerToDto(listener)) : Optional.empty();
    }

    @Override
    @Transactional
    public ListenerDTO create(ListenerDTO dto) {
        validateListenerDTO(dto);
        
        // check existing listener
        if (repository.existsById(dto.listenerId())) {
            throw new InvalidRequestException("Listener with ID '" + dto.listenerId() + "' already exists");
        }
        
        // check existing email
        if (dto.email() != null && !dto.email().isBlank()) {
            Listener existingListener = repository.findByEmail(dto.email());
            if (existingListener != null) {
                throw new InvalidRequestException("Email '" + dto.email() + "' is already registered");
            }
        }
        
        Listener listener = new Listener(
                dto.listenerId(),
                dto.displayName(),
                dto.email(),
                dto.country(),
                dto.href()
        );
        // Initialize stats with values from DTO or defaults
        listener.setTotalListeningTimeMs(dto.totalListeningTimeMs() != null ? dto.totalListeningTimeMs() : 0L);
        listener.setTotalSongsPlayed(dto.totalSongsPlayed() != null ? dto.totalSongsPlayed() : 0);
        return listenerToDto(repository.save(listener));
    }

    @Override
    @Transactional
    public ListenerDTO update(String id, ListenerDTO dto) {
        if (id == null || id.isBlank()) {
            throw new InvalidRequestException("Listener ID cannot be null or blank");
        }
        
        Listener listener = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listener", id));

        validateListenerDTO(dto);
        
        // check existing email
        if (dto.email() != null && !dto.email().isBlank() 
                && !dto.email().equals(listener.getEmail())) {
            Listener existingListener = repository.findByEmail(dto.email());
            if (existingListener != null && !existingListener.getListenerId().equals(id)) {
                throw new InvalidRequestException("Email '" + dto.email() + "' is already registered to another listener");
            }
        }
        
        listener.setDisplayName(dto.displayName());
        listener.setEmail(dto.email());
        listener.setCountry(dto.country());
        listener.setHref(dto.href());
        
        // Only update stats if provided in DTO (preserve existing if not provided)
        if (dto.totalListeningTimeMs() != null) {
            listener.setTotalListeningTimeMs(dto.totalListeningTimeMs());
        }
        if (dto.totalSongsPlayed() != null) {
            listener.setTotalSongsPlayed(dto.totalSongsPlayed());
        }
        
        return listenerToDto(repository.save(listener));
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidRequestException("Listener ID cannot be null or blank");
        }
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Listener", id);
        }
        repository.deleteById(id);
    }
    
    private void validateListenerDTO(ListenerDTO dto) {
        if (dto == null) {
            throw new InvalidRequestException("Listener data cannot be null");
        }
        
        if (dto.listenerId() == null || dto.listenerId().isBlank()) {
            throw new InvalidRequestException("Listener ID is required");
        }
        
        // validate email format
        if (dto.email() != null && !dto.email().isBlank()) {
            if (!isValidEmail(dto.email())) {
                throw new InvalidRequestException("Invalid email format: " + dto.email());
            }
        }
        
        // validate country code format
        if (dto.country() != null && !dto.country().isBlank()) {
            if (dto.country().length() > 10) {
                throw new InvalidRequestException("Country code must be 10 characters or less");
            }
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        // must contain @ and at least one dot after @
        int atIndex = email.indexOf('@');
        return atIndex > 0 && atIndex < email.length() - 1 
                && email.substring(atIndex + 1).contains(".");
    }
    
    private ListenerDTO listenerToDto(Listener listener) {
        return new ListenerDTO(
                listener.getListenerId(),
                listener.getDisplayName(),
                listener.getEmail(),
                listener.getCountry(),
                listener.getHref(),
                listener.getTotalListeningTimeMs(),
                listener.getTotalSongsPlayed()
        );
    }
}

