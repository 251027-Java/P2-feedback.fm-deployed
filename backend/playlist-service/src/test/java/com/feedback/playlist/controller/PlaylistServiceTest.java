package com.feedback.playlist.controller;

import com.feedback.playlist.controller.PlaylistController;
import com.feedback.playlist.model.Playlist;
import com.feedback.playlist.repository.PlaylistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlaylistServiceTest {

    // @Mock
    // private PlaylistRepository repository;

    // private PlaylistController controller;

    // @BeforeEach
    // public void setUp() {
    //     MockitoAnnotations.openMocks(this);
    //     controller = new PlaylistController(repository);
    // }

    // @Test
    // public void listReturnsAllWhenNoOwner() {
    //     Playlist p = new Playlist(); p.setId(1L); p.setName("All");
    //     when(repository.findAll()).thenReturn(List.of(p));

    //     List<Playlist> res = controller.list(null);
    //     assertEquals(1, res.size());
    // }

    // @Test
    // public void listByOwnerUsesRepository() {
    //     Playlist p = new Playlist(); p.setId(2L); p.setOwnerId("L1");
    //     when(repository.findByOwnerId("L1")).thenReturn(List.of(p));

    //     List<Playlist> res = controller.list("L1");
    //     assertEquals(1, res.size());
    //     assertEquals("L1", res.get(0).getOwnerId());
    // }

    // @Test
    // public void getReturnsPlaylistWhenFound() {
    //     Playlist p = new Playlist(); p.setId(3L); p.setName("Found");
    //     when(repository.findById(3L)).thenReturn(Optional.of(p));

    //     ResponseEntity<Playlist> resp = controller.get(3L);
    //     assertEquals(200, resp.getStatusCodeValue());
    //     assertEquals("Found", resp.getBody().getName());
    // }

    // @Test
    // public void createSavesAndReturnsCreated() {
    //     Playlist p = new Playlist(); p.setName("New");
    //     when(repository.save(any())).thenAnswer(i -> { Playlist x = i.getArgument(0); x.setId(7L); return x; });

    //     ResponseEntity<Playlist> resp = controller.create(p);
    //     assertEquals(201, resp.getStatusCodeValue());
    //     assertEquals(7L, resp.getBody().getId());
    // }

    // @Test
    // public void updateModifiesWhenExists() {
    //     Playlist existing = new Playlist(); existing.setId(8L); existing.setName("Old");
    //     Playlist update = new Playlist(); update.setName("New");
    //     when(repository.findById(8L)).thenReturn(Optional.of(existing));
    //     when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

    //     ResponseEntity<Playlist> resp = controller.update(8L, update);
    //     assertEquals(200, resp.getStatusCodeValue());
    //     assertEquals("New", resp.getBody().getName());
    // }

    // @Test
    // public void deleteCallsRepository() {
    //     ResponseEntity<Void> resp = controller.delete(5L);
    //     assertEquals(204, resp.getStatusCodeValue());
    //     verify(repository).deleteById(5L);
    // }
}
