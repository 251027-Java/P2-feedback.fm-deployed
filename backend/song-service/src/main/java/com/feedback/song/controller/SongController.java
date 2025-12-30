package com.feedback.song.controller;

import com.feedback.song.model.Song;
import com.feedback.song.repository.SongRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongController {
    private final SongRepository songRepository;

    public SongController(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @GetMapping
    public List<Song> list(@RequestParam(value = "q", required = false) String q) {
        if (q == null || q.isBlank()) return songRepository.findAll();
        return songRepository.findByTitleContainingIgnoreCase(q);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Song> get(@PathVariable Long id) {
        return songRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Song> create(@RequestBody Song song) {
        Song saved = songRepository.save(song);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Song> update(@PathVariable Long id, @RequestBody Song song) {
        return songRepository.findById(id).map(existing -> {
            existing.setTitle(song.getTitle());
            existing.setDurationMs(song.getDurationMs());
            return ResponseEntity.ok(songRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        songRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
