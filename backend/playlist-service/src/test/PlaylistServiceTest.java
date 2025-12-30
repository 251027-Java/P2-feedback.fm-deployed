package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.PlaylistDTO;
import com.feedback.fm.feedbackfm.model.Listener;
import com.feedback.fm.feedbackfm.model.Playlist;
import com.feedback.fm.feedbackfm.repository.ListenerRepository;
import com.feedback.fm.feedbackfm.repository.PlaylistRepository;

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
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class PlaylistServiceTest {
    
    @Mock
    private PlaylistRepository repository;

    @Mock
    private ListenerRepository listenerRepository;

    @InjectMocks
    private PlaylistServiceImpl service;

    private Playlist samplePlaylist;
    private Listener sampleListener;

    @BeforeEach
    public void setUp() {
        sampleListener = new Listener(
            "L1",
            "Test Listener",
            "user@example.com",
            "USA",
            "href"
        );

        samplePlaylist = new Playlist();
        samplePlaylist.setPlaylistId("P1");
        samplePlaylist.setName("My Playlist");
        samplePlaylist.setDescription("desc");
        samplePlaylist.setHref("href");
        samplePlaylist.setIsPublic(true);
        samplePlaylist.setOwner(sampleListener);
        samplePlaylist.setSongs(List.of());
    }

    private PlaylistDTO makeDTO() {
        return new PlaylistDTO(
            "P1",
            "My Playlist",
            "desc",
            "href",
            true,
            "L1",
            List.of()
        );
    }

    @Test
    public void testGetByIdSuccess() {
        when(repository.findById("P1")).thenReturn(Optional.of(samplePlaylist));
        
        Optional<PlaylistDTO> result = service.getById("P1");
        
        assertTrue(result.isPresent());
        assertEquals("P1", result.get().playlistId());
    }

    @Test
    public void testGetByIdNotFoundReturnsEmptyOptional() {
        when(repository.findById("UNKNOWN")).thenReturn(Optional.empty());

        Optional<PlaylistDTO> result = service.getById("UNKNOWN");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByIdInvalidInputThrows() {
        assertThrows(InvalidRequestException.class, () -> {
            service.getById("");
        });
        assertThrows(InvalidRequestException.class, () -> {
            service.getById(null);
        });
    }

    @Test
    public void testFindByNameSuccess() {
        when(repository.findByName("My Playlist")).thenReturn(List.of(samplePlaylist));

        List<PlaylistDTO> results = service.findByName("My Playlist");

        assertEquals(1, results.size());
        assertEquals("P1", results.get(0).playlistId());
    }   

    @Test
    public void testFindByNameBlankReturnsEmpty() {
        assertTrue(service.findByName("").isEmpty());
        assertTrue(service.findByName(null).isEmpty());
    }
    
    @Test
    public void testSearchByNameSuccess() {
        when(repository.findByNameContainingIgnoreCase("play"))
            .thenReturn(List.of(samplePlaylist));

        List<PlaylistDTO> results = service.searchByName("play");

        assertEquals(1, results.size());
    }

    @Test
    public void testSearchByNameBlankReturnsEmpty() {
        assertTrue(service.searchByName("").isEmpty());
        assertTrue(service.searchByName(null).isEmpty());
    }

    @Test
    public void testFindByOwnerIdSuccess() {
        when(listenerRepository.existsById("L1")).thenReturn(true);
        when(repository.findAll()).thenReturn(List.of(samplePlaylist));

        List<PlaylistDTO> results = service.findByOwnerId("L1");

        assertEquals(1, results.size());
    }

    @Test
    public void testFindByOwnerIdBlankReturnsEmpty() {
        assertTrue(service.findByOwnerId("").isEmpty());
        assertTrue(service.findByOwnerId(null).isEmpty());
    }

    @Test
    public void testFindByOwnerIdNotFoundThrows() {
        when(listenerRepository.existsById("L1")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            service.findByOwnerId("L1");
        });
    }

    @Test
    public void testFindByOwnerEmailSuccess() {
        when(repository.findByOwner_Email("user@example.com")).thenReturn(List.of(samplePlaylist));

        List<PlaylistDTO> results = service.findByOwnerEmail("user@example.com");

        assertEquals(1, results.size());
    }

    @Test
    public void testFindByOwnerEmailBlankReturnsEmpty() {
        assertTrue(service.findByOwnerEmail("").isEmpty());
        assertTrue(service.findByOwnerEmail(null).isEmpty());
    }

    @Test
    public void testFindPublicPlaylistsSuccess() {
        Playlist p2 = new Playlist();
        p2.setPlaylistId("P2");
        p2.setName("Private");
        p2.setIsPublic(false);
        p2.setSongs(List.of());

        when(repository.findAll()).thenReturn(List.of(samplePlaylist, p2));

        List<PlaylistDTO> result = service.findPublicPlaylists();

        assertEquals(1, result.size());
        assertEquals("P1", result.get(0).playlistId());
    }

    @Test
    public void testCreatePlaylistSuccess() {
        PlaylistDTO dto = makeDTO();

        when(repository.existsById("P1")).thenReturn(false);
        when(listenerRepository.findById("L1")).thenReturn(Optional.of(sampleListener));
        when(repository.save(any())).thenReturn(samplePlaylist);

        PlaylistDTO result = service.create(dto);

        assertEquals("P1", result.playlistId());
        assertEquals("My Playlist", result.name());
    }

    @Test
    public void testCreatePlaylistDuplicateIdThrows() {
        PlaylistDTO dto = makeDTO();

        when(repository.existsById("P1")).thenReturn(true);

        assertThrows(InvalidRequestException.class, () -> service.create(dto));
    }

    @Test
    public void testCreatePlaylistdOwnerNotFoundThrows() {
        PlaylistDTO dto = makeDTO();

        when(repository.existsById("P1")).thenReturn(false);
        when(listenerRepository.findById("L1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(dto));
    }

    @Test
    public void testCreatePlaylistInvalidDTOThrows() {
        PlaylistDTO dto = new PlaylistDTO("", "", "desc", "href", false, "", List.of());

        assertThrows(InvalidRequestException.class, () -> service.create(dto));
    }

    @Test
    public void testUpdatePlaylistSuccess() {
        PlaylistDTO dto = new PlaylistDTO("P1", "Updated", "New Desc", "href", true, "L1", List.of());

        when(repository.findById("P1")).thenReturn(Optional.of(samplePlaylist));
        when(listenerRepository.findById("L1")).thenReturn(Optional.of(sampleListener));
        when(repository.save(any())).thenReturn(samplePlaylist);

        PlaylistDTO result = service.update("P1", dto);

        assertEquals("P1", result.playlistId());
        assertEquals("Updated", result.name());
    }

    @Test
    public void testUpdatePlaylistNotFoundThrows() {
        PlaylistDTO dto = makeDTO();

        when(repository.findById("P1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update("P1", dto));
    }

    @Test
    public void testUpdatePlaylistOwnerNotFoundThrows() {
        PlaylistDTO dto = makeDTO();

        when(repository.findById("P1")).thenReturn(Optional.of(samplePlaylist));
        when(listenerRepository.findById("L1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update("P1", dto));
    }

    @Test
    public void testUpdatePlaylistInvalidIdThrows() {
        PlaylistDTO dto = makeDTO();

        assertThrows(InvalidRequestException.class, () -> service.update("", dto));
        assertThrows(InvalidRequestException.class, () -> service.update(null, dto));
    }

    @Test
    public void testDeletePlaylistSuccess() {
        when(repository.existsById("P1")).thenReturn(true);
        
        service.delete("P1");

        verify(repository).deleteById("P1");
    }

    @Test
    public void testDeletePlaylistNotFoundThrows() {
        when(repository.existsById("UNKNOWN")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete("UNKNOWN"));
    }

    @Test
    public void testDeletePlaylistInvalidInputThrows() {
        assertThrows(InvalidRequestException.class, () -> service.delete(""));
        assertThrows(InvalidRequestException.class, () -> service.delete(null));
    }
}
