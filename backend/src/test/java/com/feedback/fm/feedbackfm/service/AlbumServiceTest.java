package com.feedback.fm.feedbackfm.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.feedback.fm.feedbackfm.dtos.AlbumDTO;
import com.feedback.fm.feedbackfm.model.Album;
import com.feedback.fm.feedbackfm.repository.AlbumRepository;
import com.feedback.fm.feedbackfm.repository.ArtistRepository;
import com.feedback.fm.feedbackfm.service.AlbumServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private AlbumServiceImpl service;

    private Album sampleAlbum;

    @BeforeEach
    public void setup() {
        sampleAlbum = new Album("A1", "Test Album", 2020, "href");
    }

    @Test
    public void testGetAllAlbums() {
        when(albumRepository.findAll()).thenReturn(List.of(sampleAlbum));

        List<AlbumDTO> result = service.getAllAlbums();

        assertEquals(1, result.size());
        assertEquals("Test Album", result.get(0).title());
    }

    @Test
    public void testGetByIdSuccess() {
        when(albumRepository.findById("A1")).thenReturn(Optional.of(sampleAlbum));

        Optional<AlbumDTO> result = service.getById("A1");

        assertTrue(result.isPresent());
        assertEquals("A1", result.get().albumId());
    }

    @Test
    public void testGetByIdNotFound() {
        when(albumRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        Optional<AlbumDTO> result = service.getById("UNKNOWN");
        
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSearchByTitle() {
        when(albumRepository.findByTitleContainingIgnoreCase("test")).thenReturn(List.of(sampleAlbum));

        List<AlbumDTO> result = service.searchByTitle("test");

        assertEquals(1, result.size());
    }

    @Test
    public void testFindByReleaseYear() {
        when(albumRepository.findByReleaseYear(2020)).thenReturn(List.of(sampleAlbum));

        List<AlbumDTO> result = service.findByReleaseYear(2020);

        assertEquals(1, result.size());
    }

    @Test
    public void testCreateAlbum() {
        AlbumDTO dto = new AlbumDTO("A1", "New Album", 2020, "href", null, List.of());

        when(albumRepository.existsById("A1")).thenReturn(false);
        when(albumRepository.save(any())).thenReturn(sampleAlbum);
        
        AlbumDTO result = service.create(dto);

        assertEquals("A1", result.albumId());
    }

    @Test
    public void testUpdateAlbumSuccess() {
        AlbumDTO dto = new AlbumDTO("A1", "Updated", 2021, "href", null, List.of());
        
        when(albumRepository.findById("A1")).thenReturn(Optional.of(sampleAlbum));
        when(albumRepository.save(any())).thenReturn(sampleAlbum);

        AlbumDTO result = service.update("A1", dto);

        assertEquals("A1", result.albumId());
    }

    @Test
    public void testUpdateAlbumNotFound() {
        AlbumDTO dto = new AlbumDTO("A1", "Updated", 2021, "href", null, List.of());

        when(albumRepository.findById("A1")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.update("A1", dto));
    }

    @Test
    public void testDeleteAlbumSuccess() {
        when(albumRepository.existsById("A1")).thenReturn(true);

        service.delete("A1");

        verify(albumRepository).deleteById("A1");
    }

    @Test
    public void testDeleteAlbumNotFound() {
        when(albumRepository.existsById("UNKNOWN")).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> service.delete("UNKNOWN"));
    }
}

