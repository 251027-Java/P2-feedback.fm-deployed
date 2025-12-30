package com.feedback.artist.controller;

import com.feedback.artist.model.Artist;
import com.feedback.artist.repository.ArtistRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {
    private final ArtistRepository artistRepository;

    public ArtistController(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @GetMapping
    public List<Artist> list(@RequestParam(value = "q", required = false) String q) {
        if (q == null || q.isBlank()) return artistRepository.findAll();
        return artistRepository.findByNameContainingIgnoreCase(q);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artist> get(@PathVariable Long id) {
        return artistRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Artist> create(@RequestBody Artist artist) {
        Artist saved = artistRepository.save(artist);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Artist> update(@PathVariable Long id, @RequestBody Artist artist) {
        return artistRepository.findById(id).map(existing -> {
            existing.setName(artist.getName());
            return ResponseEntity.ok(artistRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        artistRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
