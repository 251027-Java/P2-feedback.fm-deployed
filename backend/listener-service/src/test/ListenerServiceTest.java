package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.ListenerDTO;
import com.feedback.fm.feedbackfm.model.Listener;
import com.feedback.fm.feedbackfm.repository.ListenerRepository;

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

@ExtendWith(MockitoExtension.class)
public class ListenerServiceTest {

    @Mock
    private ListenerRepository repository;

    @InjectMocks
    private ListenerServiceImpl service;

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
    }

    public ListenerDTO makeDTO() {
        return new ListenerDTO(
            "L1",
            "Test Listener",
            "user@example.com",
            "USA",
            "href"
        );
    }

    @Test
    public void testGetAllListeners() {
        when(repository.findAll()).thenReturn(List.of(sampleListener));

        List<ListenerDTO> result = service.getAllListeners();

        assertEquals(1, result.size());
        assertEquals("L1", result.get(0).listenerId());
    }

    @Test
    public void testGetByIdSuccess() {
        when(repository.findById("L1")).thenReturn(Optional.of(sampleListener));

        Optional<ListenerDTO> result = service.getById("L1");

        assertTrue(result.isPresent());
        assertEquals("L1", result.get().listenerId());
    }

    @Test
    public void testGetByIdNotFoundReturnsEmptyOptional() {
        when(repository.findById("UNKNOWN")).thenReturn(Optional.empty());

        Optional<ListenerDTO> result = service.getById("UNKNOWN");

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
    public void testFindByDisplayNameSuccess() {
        when(repository.findByDisplayName("Test Listener")).thenReturn(List.of(sampleListener));

        List<ListenerDTO> result = service.findByDisplayName("Test Listener");

        assertEquals(1, result.size());
        assertEquals("L1", result.get(0).listenerId());
    }

    @Test
    public void testFindByDisplayNameBlankReturnsEmpty() {
        assertTrue(service.findByDisplayName("").isEmpty());
        assertTrue(service.findByDisplayName(null).isEmpty());
    }

    @Test
    public void testSearchByDisplayNameSuccess() {
        when(repository.findByDisplayNameContainingIgnoreCase("test")).thenReturn(List.of(sampleListener));

        List<ListenerDTO> result = service.searchByDisplayName("test");

        assertEquals(1, result.size());
    }

    @Test
    public void testSearchByDisplayNameBlankReturnsEmpty() {
        assertTrue(service.searchByDisplayName("").isEmpty());
        assertTrue(service.searchByDisplayName(null).isEmpty());
    }

    @Test
    public void testFindByEmailSuccess() {
        when(repository.findByEmail("user@example.com")).thenReturn(sampleListener);

        Optional<ListenerDTO> result = service.findByEmail("user@example.com");

        assertTrue(result.isPresent());
        assertEquals("L1", result.get().listenerId());

    }

    @Test
    public void testFindByEmailNotFoundReturnsEmpty() {
        when(repository.findByEmail("missing@example.com")).thenReturn(null);

        Optional<ListenerDTO> result = service.findByEmail("missing@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindByEmailInvalidInputThrows() {
        assertThrows(InvalidRequestException.class, () -> service.findByEmail(""));
        assertThrows(InvalidRequestException.class, () -> service.findByEmail(null));
    }

    @Test
    public void testCreateListenerSuccess() {
        ListenerDTO dto = makeDTO();

        when(repository.existsById("L1")).thenReturn(false);
        when(repository.findByEmail("user@example.com")).thenReturn(null);
        when(repository.save(any())).thenReturn(sampleListener);

        ListenerDTO result = service.create(dto);

        assertEquals("L1", result.listenerId());
        assertEquals("Test Listener", result.displayName());
    }

    @Test
    public void testCreateListenerDuplicateIdThrows() {
        ListenerDTO dto = makeDTO();

        when(repository.existsById("L1")).thenReturn(true);

        assertThrows(InvalidRequestException.class, () -> service.create(dto));
    }

    @Test
    public void testCreateListenerDuplicateEmailThrows() {
        ListenerDTO dto = makeDTO();

        when(repository.existsById("L1")).thenReturn(false);
        when(repository.findByEmail("user@example.com")).thenReturn(sampleListener);

        assertThrows(InvalidRequestException.class, () -> service.create(dto));
    }

    @Test
    public void testCreateListenerInvalidDTOThrows() {
        ListenerDTO dto = new ListenerDTO("", "", "bademail", "USA", "href");

        assertThrows(InvalidRequestException.class, () -> service.create(dto));
    }

    @Test
    public void testUpdateListenerSuccess() {
        ListenerDTO dto = new ListenerDTO("L1", "Updated Listener", "updated@example.com", "USA", "href");

        when(repository.findById("L1")).thenReturn(Optional.of(sampleListener));
        when(repository.findByEmail("updated@example.com")).thenReturn(null);
        when(repository.save(any())).thenReturn(sampleListener);

        ListenerDTO result = service.update("L1", dto);

        assertEquals("L1", result.listenerId());
        assertEquals("Updated Listener", result.displayName());
    }

    @Test
    public void testUpdateListenerNotFoundThrows() {
        ListenerDTO dto = makeDTO();
        
        when(repository.findById("L1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update("L1", dto));
    }

    @Test
    public void testUpdateListenerDuplicateEmailThrows() {
        ListenerDTO dto = new ListenerDTO("L1", "Test", "new@example.com", "USA", "href");
        Listener anotherListener = new Listener("L2", "Other", "new@example.com", "USA", "href");
        
        when(repository.findById("L1")).thenReturn(Optional.of(sampleListener));
        when(repository.findByEmail("new@example.com")).thenReturn(anotherListener);

        assertThrows(InvalidRequestException.class, () -> service.update("L1", dto));
    }

    @Test
    public void testUpdateListenerInvalidIdThrows() {
        ListenerDTO dto = makeDTO();

        assertThrows(InvalidRequestException.class, () -> service.update("", dto));
        assertThrows(InvalidRequestException.class, () -> service.update(null, dto));
    }

    @Test
    public void testDeleteListenerSuccess() {
        when(repository.existsById("L1")).thenReturn(true);
        
        service.delete("L1");

        verify(repository).deleteById("L1");
    }

    @Test
    public void testDeleteListenerNotFoundThrows() {
        when(repository.existsById("UNKNOWN")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete("UNKNOWN"));
    }

    @Test
    public void testDeleteListenerInvalidInputThrows() {
        assertThrows(InvalidRequestException.class, () -> service.delete(""));
        assertThrows(InvalidRequestException.class, () -> service.delete(null));
    }
}