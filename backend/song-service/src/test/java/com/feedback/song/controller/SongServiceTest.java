package com.feedback.song.controller;

import com.feedback.song.controller.SongController;
import com.feedback.song.model.Song;
import com.feedback.song.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SongServiceTest {

    // @Mock
    // private SongRepository repository;

    // private SongController controller;

    // @BeforeEach
    // public void setUp() {
    //     MockitoAnnotations.openMocks(this);
    //     controller = new SongController(repository);
    // }

    // @Test
    // public void listReturnsAllWhenNoQuery() {
    //     Song s = new Song(); s.setId(1L); s.setTitle("All Song");
    //     when(repository.findAll()).thenReturn(List.of(s));

    //     List<Song> res = controller.list(null);
    //     assertEquals(1, res.size());
    // }

    // @Test
    // public void listWithQueryUsesSearch() {
    //     Song s = new Song(); s.setId(2L); s.setTitle("Rock Song");
    //     when(repository.findByTitleContainingIgnoreCase("rock")).thenReturn(List.of(s));

    //     List<Song> res = controller.list("rock");
    //     assertEquals(1, res.size());
    //     assertTrue(res.get(0).getTitle().contains("Rock"));
    // }

    // @Test
    // public void getReturnsSongWhenFound() {
    //     Song s = new Song(); s.setId(3L); s.setTitle("Found");
    //     when(repository.findById(3L)).thenReturn(Optional.of(s));

    //     ResponseEntity<Song> resp = controller.get(3L);
    //     assertEquals(200, resp.getStatusCodeValue());
    //     assertEquals("Found", resp.getBody().getTitle());
    // }

    // @Test
    // public void createSavesAndReturnsCreated() {
    //     Song s = new Song(); s.setTitle("New");
    //     when(repository.save(any())).thenAnswer(i -> { Song x = i.getArgument(0); x.setId(7L); return x; });

    //     ResponseEntity<Song> resp = controller.create(s);
    //     assertEquals(201, resp.getStatusCodeValue());
    //     assertEquals(7L, resp.getBody().getId());
    // }

    // @Test
    // public void updateModifiesWhenExists() {
    //     Song existing = new Song(); existing.setId(8L); existing.setTitle("Old");
    //     Song update = new Song(); update.setTitle("New");
    //     when(repository.findById(8L)).thenReturn(Optional.of(existing));
    //     when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

    //     ResponseEntity<Song> resp = controller.update(8L, update);
    //     assertEquals(200, resp.getStatusCodeValue());
    //     assertEquals("New", resp.getBody().getTitle());
    // }

    // @Test
    // public void deleteCallsRepository() {
    //     ResponseEntity<Void> resp = controller.delete(5L);
    //     assertEquals(204, resp.getStatusCodeValue());
    //     verify(repository).deleteById(5L);
    // }
}
