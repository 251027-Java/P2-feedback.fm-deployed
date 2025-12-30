package com.feedback.playlist.controller;

import com.feedback.playlist.model.Playlist;
import com.feedback.playlist.repository.PlaylistRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {
    private final PlaylistRepository playlistRepository;

    public PlaylistController(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    @GetMapping
    public List<Playlist> list(@RequestParam(value = "ownerId", required = false) String ownerId) {
        if (ownerId == null || ownerId.isBlank()) return playlistRepository.findAll();
        return playlistRepository.findByOwnerId(ownerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Playlist> get(@PathVariable Long id) {
        return playlistRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Playlist> create(@RequestBody Playlist playlist) {
        Playlist saved = playlistRepository.save(playlist);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Playlist> update(@PathVariable Long id, @RequestBody Playlist playlist) {
        return playlistRepository.findById(id).map(existing -> {
            existing.setName(playlist.getName());
            return ResponseEntity.ok(playlistRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        playlistRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
