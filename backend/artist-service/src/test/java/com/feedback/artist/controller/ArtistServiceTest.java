package com.feedback.artist.controller;

import com.feedback.artist.controller.ArtistController;
import com.feedback.artist.model.Artist;
import com.feedback.artist.repository.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArtistServiceTest {

    // @Mock
    // private ArtistRepository repository;

    // private ArtistController controller;

    // @BeforeEach
    // public void setUp() {
    //     MockitoAnnotations.openMocks(this);
    //     controller = new ArtistController(repository);
    // }

    // @Test
    // public void listReturnsAllWhenNoQuery() {
    //     Artist a = new Artist(); a.setId(1L); a.setName("Artist One");
    //     when(repository.findAll()).thenReturn(List.of(a));

    //     List<Artist> res = controller.list(null);
    //     assertEquals(1, res.size());
    //     assertEquals("Artist One", res.get(0).getName());
    // }

    // @Test
    // public void listWithQueryUsesSearch() {
    //     Artist a = new Artist(); a.setId(2L); a.setName("Query Artist");
    //     when(repository.findByNameContainingIgnoreCase("q")).thenReturn(List.of(a));

    //     List<Artist> res = controller.list("q");
    //     assertEquals(1, res.size());
    //     assertEquals("Query Artist", res.get(0).getName());
    // }

    // @Test
    // public void getReturnsArtistWhenFound() {
    //     Artist a = new Artist(); a.setId(3L); a.setName("Found");
    //     when(repository.findById(3L)).thenReturn(Optional.of(a));

    //     ResponseEntity<Artist> resp = controller.get(3L);
    //     assertEquals(200, resp.getStatusCodeValue());
    //     assertEquals("Found", resp.getBody().getName());
    // }

    // @Test
    // public void createReturnsCreated() {
    //     Artist a = new Artist(); a.setName("New");
    //     when(repository.save(any())).thenAnswer(i -> { Artist x = i.getArgument(0); x.setId(7L); return x; });

    //     ResponseEntity<Artist> resp = controller.create(a);
    //     assertEquals(201, resp.getStatusCodeValue());
    //     assertEquals(7L, resp.getBody().getId());
    // }

    // @Test
    // public void updateModifiesWhenExists() {
    //     Artist existing = new Artist(); existing.setId(8L); existing.setName("Old");
    //     Artist update = new Artist(); update.setName("New");
    //     when(repository.findById(8L)).thenReturn(Optional.of(existing));
    //     when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

    //     ResponseEntity<Artist> resp = controller.update(8L, update);
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
