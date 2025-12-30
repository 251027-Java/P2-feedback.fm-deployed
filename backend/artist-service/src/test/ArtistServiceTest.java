package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.ArtistDTO;
import com.feedback.fm.feedbackfm.model.Artist;
import com.feedback.fm.feedbackfm.model.Album;
import com.feedback.fm.feedbackfm.repository.ArtistRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.feedback.fm.feedbackfm.exception.InvalidRequestException;
import com.feedback.fm.feedbackfm.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceTest {
    @Mock
    private ArtistRepository repository;

    @InjectMocks
    private ArtistServiceImpl service;

    private Artist sampleArtist;

    @BeforeEach
    public void setup() {
        sampleArtist = new Artist("A1", "Test Artist", "href");

        Album album = new Album("ALB1", "Test Album", 2020, "href");
        sampleArtist.setAlbums(Set.of(album));
    }

    public ArtistDTO makeDTO() {
        return new ArtistDTO("A1", "Test Artist", "href", List.of("ALB1"));
    }

    @Test
    public void testGetAllArtists() {
        when(repository.findAll()).thenReturn(List.of(sampleArtist));

       List<ArtistDTO> result = service.getAllArtists();
       
       assertEquals(1, result.size());
       assertEquals("A1", result.get(0).artistId());
    }

    @Test
    public void testGetByIdSuccess() {
        when(repository.findById("A1")).thenReturn(Optional.of(sampleArtist));

        Optional<ArtistDTO> result = service.getById("A1");

        assertTrue(result.isPresent());
        assertEquals("A1", result.get().artistId());
    }

    @Test
    public void testGetByIdNotFoundReturnsEmptyOptional() {
        when(repository.findById("UNKNOWN")).thenReturn(Optional.empty());

        Optional<ArtistDTO> result = service.getById("UNKNOWN");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByIdInvalidInputThrows() {
        assertThrows(InvalidRequestException.class, () -> service.getById(""));
        assertThrows(InvalidRequestException.class, () -> service.getById(null));
    }

    @Test
    public void testFindByNameSuccess() {
        when(repository.findByName("Test Artist")).thenReturn(List.of(sampleArtist));

        List<ArtistDTO> result = service.findByName("Test Artist");

        assertEquals(1, result.size());
        assertEquals("A1", result.get(0).artistId());
    }

    @Test
    public void testFindByNameBlankReturnsEmpty() {
        assertTrue(service.findByName("").isEmpty());
        assertTrue(service.findByName(null).isEmpty());
    }

    @Test
    public void testSearchByNameSuccess() {
        when(repository.findByNameContainingIgnoreCase("test")).thenReturn(List.of(sampleArtist));

        List<ArtistDTO> result = service.searchByName("test");

        assertEquals(1, result.size());
    }

    @Test
    public void testSearchByNameBlankReturnsEmpty() {
        assertTrue(service.searchByName("").isEmpty());
        assertTrue(service.searchByName(null).isEmpty());
    }

    @Test
    public void testCreateArtistSuccess() {
        ArtistDTO dto = makeDTO();

        when(repository.existsById("A1")).thenReturn(false);
        when(repository.save(any())).thenReturn(sampleArtist);

        ArtistDTO result = service.create(dto);

        assertEquals("A1", result.artistId());
        assertEquals("Test Artist", result.name());
    }

    @Test
    public void testCreateArtistDuplicateThrows() {
        ArtistDTO dto = makeDTO();

        when(repository.existsById("A1")).thenReturn(true);

        assertThrows(InvalidRequestException.class, () -> service.create(dto));
    }

    @Test
    public void testCreateArtistInvalidDTOThrows() {
        ArtistDTO dto = new ArtistDTO("", "", "href", List.of());

        assertThrows(InvalidRequestException.class, () -> service.create(dto));
    }

    @Test
    public void testUpdateArtistSuccess() {
        ArtistDTO dto = new ArtistDTO("A1", "Updated Artist", "newHref", List.of("ALB1"));

        when(repository.findById("A1")).thenReturn(Optional.of(sampleArtist));
        when(repository.save(any())).thenReturn(sampleArtist);  

        ArtistDTO result = service.update("A1", dto);

        assertEquals("A1", result.artistId());
        assertEquals("Updated Artist", result.name());
    }

    @Test
    public void testUpdateArtistNotFoundThrows() {
        ArtistDTO dto = makeDTO();

        when(repository.findById("A1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update("A1", dto));
    }

    @Test
    public void testUpdateArtistInvalidDTOThrows() {
        ArtistDTO dto = makeDTO();

        assertThrows(InvalidRequestException.class, () -> service.update("", dto));
        assertThrows(InvalidRequestException.class, () -> service.update(null, dto));
    }

    @Test
    public void testDeleteArtistSuccess() {
        when(repository.existsById("A1")).thenReturn(true);

        service.delete("A1");

        verify(repository).deleteById("A1");
    }

    @Test
    public void testDeleteArtistNotFoundThrows() {
        when(repository.existsById("UNKNOWN")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete("UNKNOWN"));
    }

    @Test
    public void testDeleteArtistInvalidInputThrows() {
        assertThrows(InvalidRequestException.class, () -> service.delete(""));
        assertThrows(InvalidRequestException.class, () -> service.delete(null));
    }
}