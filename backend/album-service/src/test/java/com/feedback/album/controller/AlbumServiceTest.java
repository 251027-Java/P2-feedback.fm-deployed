package com.feedback.album.controller;

import com.feedback.album.controller.AlbumController;
import com.feedback.album.model.Album;
import com.feedback.album.repository.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    private AlbumController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new AlbumController(albumRepository);
    }

    @Test
    public void listReturnsAllWhenNoQuery() {
        Album a = new Album();
        a.setId(1L);
        a.setTitle("Test Album");
        a.setYear(2020);

        when(albumRepository.findAll()).thenReturn(List.of(a));

        List<Album> res = controller.list(null);

        assertEquals(1, res.size());
        assertEquals("Test Album", res.get(0).getTitle());
    }

    @Test
    public void listWithQueryUsesRepositorySearch() {
        Album a = new Album(); a.setId(2L); a.setTitle("Query Album");

        when(albumRepository.findByTitleContainingIgnoreCase("q")).thenReturn(List.of(a));

        List<Album> res = controller.list("q");

        assertEquals(1, res.size());
        assertEquals("Query Album", res.get(0).getTitle());
    }

    @Test
    public void getReturnsAlbumWhenFound() {
        Album a = new Album(); a.setId(3L); a.setTitle("Found");
        when(albumRepository.findById(3L)).thenReturn(Optional.of(a));

        ResponseEntity<Album> resp = controller.get(3L);

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals("Found", resp.getBody().getTitle());
    }

    @Test
    public void getReturnsNotFoundWhenMissing() {
        when(albumRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Album> resp = controller.get(99L);

        assertEquals(404, resp.getStatusCodeValue());
    }

    @Test
    public void createSavesAndReturnsCreated() {
        Album a = new Album(); a.setTitle("New");

        when(albumRepository.save(any())).thenAnswer(i -> { Album x = i.getArgument(0); x.setId(7L); return x; });

        ResponseEntity<Album> resp = controller.create(a);

        assertEquals(201, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals(7L, resp.getBody().getId());
    }

    @Test
    public void updateModifiesWhenExists() {
        Album existing = new Album(); existing.setId(8L); existing.setTitle("Old");
        Album update = new Album(); update.setTitle("New"); update.setYear(2022);

        when(albumRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(albumRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ResponseEntity<Album> resp = controller.update(8L, update);

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals("New", resp.getBody().getTitle());
    }

    @Test
    public void deleteCallsRepository() {
        ResponseEntity<Void> resp = controller.delete(5L);

        assertEquals(204, resp.getStatusCodeValue());
        verify(albumRepository).deleteById(5L);
    }
}
