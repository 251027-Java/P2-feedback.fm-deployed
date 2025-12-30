package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.SongDTO;
import com.feedback.fm.feedbackfm.model.Song;
import com.feedback.fm.feedbackfm.model.Artist;
import com.feedback.fm.feedbackfm.model.Album;
import com.feedback.fm.feedbackfm.repository.SongRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import com.feedback.fm.feedbackfm.exception.InvalidRequestException;
import com.feedback.fm.feedbackfm.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SongServiceTest {
    
    @Mock
    private SongRepository repository;

    @InjectMocks
    private SongServiceImpl service;

    private Song sampleSong;

    @BeforeEach
    public void setup() {
        Artist artist = new Artist("AR1", "Artist One", "artistHref");
        Album album = new Album("ALB1", "Album One", 2020, "albumHref");

        sampleSong = new Song("S1", "Song One", 200000, "songHref");
        sampleSong.setArtists(Set.of(artist));
        sampleSong.setAlbums(Set.of(album));
    }

    public SongDTO makeDTO() {
        return new SongDTO(
            "S1",
            "Song One",
            "songHref",
             200000,
            List.of("AR1"),
            List.of("ALB1")
        );
    }

    @Test
    public void testGetAllSongs() {
        when(repository.findAll()).thenReturn(List.of(sampleSong));

        List<SongDTO> result = service.getAllSongs();

        assertEquals(1, result.size());
        assertEquals("S1", result.get(0).songId());
    }

    @Test
    public void testGetByIdSuccess() {
        when(repository.findById("S1")).thenReturn(Optional.of(sampleSong));

        Optional<SongDTO> result = service.getById("S1");

        assertTrue(result.isPresent());
        assertEquals("S1", result.get().songId());
    }

    @Test
    public void testGetByIdNotFoundReturnsEmptyOptional() {
        when(repository.findById("UNKNOWN")).thenReturn(Optional.empty());

        Optional<SongDTO> result = service.getById("UNKNOWN");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByIdInvalidInputThrows() {
        assertThrows(InvalidRequestException.class, () -> service.getById(""));
        assertThrows(InvalidRequestException.class, () -> service.getById(null));
    }

    @Test
    public void testFindByNameSuccess() {
        when(repository.findByName("Song One")).thenReturn(List.of(sampleSong));

        List<SongDTO> result = service.findByName("Song One");

        assertEquals(1, result.size());
        assertEquals("S1", result.get(0).songId());
    }

    @Test
    public void testFindByNameBlankReturnsEmpty() {
        assertTrue(service.findByName("").isEmpty());
        assertTrue(service.findByName(null).isEmpty());
    }

    @Test
    public void testSearchByNameSuccess() {
        when(repository.findByNameContainingIgnoreCase("song")).thenReturn(List.of(sampleSong));

        List<SongDTO> result = service.searchByName("song");

        assertEquals(1, result.size());
    }

    @Test
    public void testSearchByNameBlankReturnsEmpty() {
        assertTrue(service.searchByName("").isEmpty());
        assertTrue(service.searchByName(null).isEmpty());
    }

    @Test
    public void testFindByArtistNameSuccess() {
        when(repository.findByArtists_Name("Artist One")).thenReturn(List.of(sampleSong));

        List<SongDTO> result = service.findByArtistName("Artist One");

        assertEquals(1, result.size());
    }

    @Test
    public void testFindByArtistNameBlankReturnsEmpty() {
        assertTrue(service.findByArtistName("").isEmpty());
        assertTrue(service.findByArtistName(null).isEmpty());
    }

    @Test
    public void testFindByDurationSuccess() {
        when(repository.findByDurationMs(200000)).thenReturn(List.of(sampleSong));

        List<SongDTO> result = service.findByDuration(200000);

        assertEquals(1, result.size());
    }

    @Test
    public void testFindByDurationInvalidReturnsEmpty() {
        assertTrue(service.findByDuration(null).isEmpty());
        assertTrue(service.findByDuration(0).isEmpty());
        assertTrue(service.findByDuration(-10).isEmpty());
    }

    @Test
    public void testFindByDurationRangeSuccess() {
        when(repository.findByDurationMsBetween(1000, 300000)).thenReturn(List.of(sampleSong));

        List<SongDTO> result = service.findByDurationRange(1000, 300000);

        assertEquals(1, result.size());
    }

    @Test
    public void testFindByDurationRangeInvalidThrows() {
        assertThrows(InvalidRequestException.class, () -> service.findByDurationRange(null, 100));
        assertThrows(InvalidRequestException.class, () -> service.findByDurationRange(100, null));
        assertThrows(InvalidRequestException.class, () -> service.findByDurationRange(-1, 100));
        assertThrows(InvalidRequestException.class, () -> service.findByDurationRange(200, 100));
    }

    @Test
    public void testCreateSongSuccess() {
        SongDTO dto = makeDTO();

        when(repository.existsById("S1")).thenReturn(false);
        when(repository.save(any())).thenReturn(sampleSong);

        SongDTO result = service.create(dto);

        assertEquals("S1", result.songId());
        assertEquals("Song One", result.name());
    }

    @Test
    public void testCreateSongDuplicateThrows() {
        SongDTO dto = makeDTO();

        when(repository.existsById("S1")).thenReturn(true);

        assertThrows(InvalidRequestException.class, () -> service.create(dto));
    }

    @Test
    public void testCreateSongInvalidDTOThrows() {
        SongDTO dto = new SongDTO("", "", "href", -5, List.of(), List.of());

        assertThrows(InvalidRequestException.class, () -> service.create(dto));
    }

    @Test
    public void testUpdateSongSuccess() {
        SongDTO dto = new SongDTO("S1", "Updated Song", "newHref", 123456, List.of("AR1"), List.of("ALB1"));

        when(repository.findById("S1")).thenReturn(Optional.of(sampleSong));
        when(repository.save(any())).thenReturn(sampleSong);

        SongDTO result = service.update("S1", dto);

        assertEquals("S1", result.songId());
        assertEquals("Updated Song", result.name());
    }

    @Test
    public void testUpdateSongNotFoundThrows() {
        SongDTO dto = makeDTO();

        when(repository.findById("S1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update("S1", dto));
    }

    @Test
    public void testUpdateSongInvalidDTOThrows() {
        SongDTO dto = makeDTO();

        assertThrows(InvalidRequestException.class, () -> service.update("", dto));
        assertThrows(InvalidRequestException.class, () -> service.update(null, dto));
    }

    @Test
    public void testDeleteSongSuccess() {
        when(repository.existsById("S1")).thenReturn(true);

        service.delete("S1");

        verify(repository).deleteById("S1");
    }

    @Test
    public void testDeleteSongNotFoundThrows() {
        when(repository.existsById("UNKNOWN")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete("UNKNOWN"));
    }

    @Test
    public void testDeleteSongInvalidInputThrows() {
        assertThrows(InvalidRequestException.class, () -> service.delete(""));
        assertThrows(InvalidRequestException.class, () -> service.delete(null));
    }
}