package com.feedback.album.controller;

import com.feedback.album.model.Album;
import com.feedback.album.repository.AlbumRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {
    private final AlbumRepository albumRepository;

    public AlbumController(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @GetMapping
    public List<Album> list(@RequestParam(value = "q", required = false) String q) {
        if (q == null || q.isBlank()) return albumRepository.findAll();
        return albumRepository.findByTitleContainingIgnoreCase(q);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> get(@PathVariable Long id) {
        return albumRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Album> create(@RequestBody Album album) {
        Album saved = albumRepository.save(album);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Album> update(@PathVariable Long id, @RequestBody Album album) {
        return albumRepository.findById(id).map(existing -> {
            existing.setTitle(album.getTitle());
            existing.setYear(album.getYear());
            return ResponseEntity.ok(albumRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        albumRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
