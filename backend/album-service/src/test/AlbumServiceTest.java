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
import com.feedback.fm.feedbackfm.exception.InvalidRequestException;
import com.feedback.fm.feedbackfm.exception.ResourceNotFoundException;

import com.feedback.fm.feedbackfm.dtos.AlbumDTO;
import com.feedback.fm.feedbackfm.model.Album;
import com.feedback.fm.feedbackfm.model.Artist;
import com.feedback.fm.feedbackfm.repository.AlbumRepository;
import com.feedback.fm.feedbackfm.repository.ArtistRepository;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private AlbumServiceImpl service;

    private Album sampleAlbum;
    private Artist sampleArtist;

    @BeforeEach
    public void setup() {
        sampleAlbum = new Album("A1", "Test Album", 2020, "href");
        sampleArtist = new Artist("ART1", "Test Artist","href");
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
    public void testGetByIdNotFoundReturnsEmptyOptional() {
        when(albumRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        Optional<AlbumDTO> result = service.getById("UNKNOWN");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByIdInvalidInputThrows() {
        assertThrows(InvalidRequestException.class, () -> service.getById(""));
        assertThrows(InvalidRequestException.class, () -> service.getById(null));
    }

    @Test
    public void testSearchByTitle() {
        when(albumRepository.findByTitleContainingIgnoreCase("test")).thenReturn(List.of(sampleAlbum));

        List<AlbumDTO> result = service.searchByTitle("test");

        assertEquals(1, result.size());
    }

    @Test
    public void testSearchByTitleBlankReturnsEmpty() {
        assertTrue(service.searchByTitle("").isEmpty());
        assertTrue(service.searchByTitle(null).isEmpty());
    }

    @Test
    public void testFindByReleaseYearSuccess() {
        when(albumRepository.findByReleaseYear(2020)).thenReturn(List.of(sampleAlbum));

        List<AlbumDTO> result = service.findByReleaseYear(2020);

        assertEquals(1, result.size());
    }

    @Test
    public void testFindByReleaseYearNullReturnsEmpty() {
        assertTrue(service.findByReleaseYear(null).isEmpty());
    }

    @Test
    void testFindByReleaseYearInvalidThrows() {
        assertThrows(InvalidRequestException.class, () -> service.findByReleaseYear(1800));
        assertThrows(InvalidRequestException.class, () -> service.findByReleaseYear(2500));
    }

    @Test
    public void testFindByArtistIdSuccess() {
        sampleAlbum.setArtist(sampleArtist);

        when(artistRepository.existsById("ART1")).thenReturn(true);
        when(albumRepository.findAll()).thenReturn(List.of(sampleAlbum));

        List<AlbumDTO> result = service.findByArtistId("ART1");

        assertEquals(1, result.size());
    }

    @Test
    public void testFindByArtistIdBlankReturnsEmpty() {
        assertTrue(service.findByArtistId("").isEmpty());
        assertTrue(service.findByArtistId(null).isEmpty());
    }

    @Test
    public void testFindByArtistIdNotFoundThrows() {
        when(artistRepository.existsById("ART1")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.findByArtistId("ART1"));
    }

    @Test
    public void testCreateAlbumSuccess() {
        AlbumDTO dto = new AlbumDTO("A1", "New Album", 2020, "href", null, List.of());

        when(albumRepository.existsById("A1")).thenReturn(false);
        when(albumRepository.save(any())).thenReturn(sampleAlbum);
        
        AlbumDTO result = service.create(dto);

        assertEquals("A1", result.albumId());
    }

    @Test
    public void testCreateAlbumDuplicateThrows() {
        AlbumDTO dto = new AlbumDTO("A1", "New Album", 2020, "href", null, List.of());

        when(albumRepository.existsById("A1")).thenReturn(true);

        assertThrows(InvalidRequestException.class, () -> service.create(dto));
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
    public void testUpdateAlbumNotFoundThrows() {
        AlbumDTO dto = new AlbumDTO("A1", "Updated", 2021, "href", null, List.of());

        when(albumRepository.findById("A1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update("A1", dto));
    }

    @Test
    public void testDeleteAlbumSuccess() {
        when(albumRepository.existsById("A1")).thenReturn(true);

        service.delete("A1");

        verify(albumRepository).deleteById("A1");
    }

    @Test
    public void testDeleteAlbumNotFoundThrows() {
        when(albumRepository.existsById("UNKNOWN")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete("UNKNOWN"));
    }
}
