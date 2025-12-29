package com.feedback.fm.feedbackfm.service;

import com.feedback.fm.feedbackfm.dtos.HistoryDTO;
import com.feedback.fm.feedbackfm.model.History;
import com.feedback.fm.feedbackfm.model.Listener;
import com.feedback.fm.feedbackfm.model.Song;
import com.feedback.fm.feedbackfm.repository.HistoryRepository;
import com.feedback.fm.feedbackfm.repository.ListenerRepository;
import com.feedback.fm.feedbackfm.repository.SongRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import com.feedback.fm.feedbackfm.exception.InvalidRequestException;
import com.feedback.fm.feedbackfm.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HistoryServiceTest {

    @Mock
    private HistoryRepository repository;

    @Mock
    private ListenerRepository listenerRepository;

    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private HistoryServiceImpl service;

    private Listener listener;
    private Song song;
    private History history;

    @BeforeEach
    public void setUp() {
        listener = new Listener(
            "L1",
            "Listener One",
            "listener@email.com",
            "US",
            "listenerHref"
        );

        song = new Song("S1", "Song One", 200000, "songHref");

        history = new History();
        history.setHistoryId(1L);
        history.setPlayedAt(LocalDateTime.now().minusHours(1));
        history.setListener(listener);
        history.setSong(song);
    }

    private HistoryDTO makeDTO() {
        return new HistoryDTO(
            1L,
            LocalDateTime.now().minusHours(1),
            "L1",
            "S1"
        );
    }

    @Test
    public void testGetAllHistory() {
        when(repository.findAll()).thenReturn(List.of(history));

        List<HistoryDTO> result = service.getAllHistory();

        assertEquals(1, result.size());
        assertEquals("L1", result.get(0).listenerId());
    }

    @Test
    public void testGetByIdSuccess() {
        when(repository.findById(1L)).thenReturn(Optional.of(history));

        Optional<HistoryDTO> result = service.getById(1L);

        assertTrue(result.isPresent());
        assertEquals("L1", result.get().listenerId());
    }

    @Test
    public void testGetByIdNotFoundReturnsEmpty() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<HistoryDTO> result = service.getById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByIdInvalidThrows() {
        assertThrows(InvalidRequestException.class, () -> service.getById(null));
    }

    @Test
    public void testFindByListenerIdSuccess() {
        when(repository.findByListener_ListenerId("L1")).thenReturn(List.of(history));

        List<HistoryDTO> result = service.findByListenerId("L1");

        assertEquals(1, result.size());
        assertEquals("S1", result.get(0).songId());
    }

    @Test
    public void testFindByListenerIdBlankReturnsEmpty() {
        assertTrue(service.findByListenerId("").isEmpty());
        assertTrue(service.findByListenerId(null).isEmpty());
    }

    @Test
    public void testFindBySongIdSuccess() {
        when(repository.findBySong_SongId("S1")).thenReturn(List.of(history));

        List<HistoryDTO> result = service.findBySongId("S1");

        assertEquals(1, result.size());
        assertEquals("L1", result.get(0).listenerId());
    }

    @Test
    public void testFindBySongIdBlankReturnsEmpty() {
        assertTrue(service.findBySongId("").isEmpty());
        assertTrue(service.findBySongId(null).isEmpty());
    }

    @Test
    public void testFindByListenerIdAndSongIdSuccess() {
        when(repository.findByListener_ListenerIdAndSong_SongId("L1", "S1")).thenReturn(List.of(history));

        List<HistoryDTO> result = service.findByListenerIdAndSongId("L1", "S1");

        assertEquals(1, result.size());
    }

    @Test
    public void testFindByListenerIdAndSongIdBlankReturnsEmpty() {
        assertTrue(service.findByListenerIdAndSongId("", "S1").isEmpty());
        assertTrue(service.findByListenerIdAndSongId("L1", "").isEmpty());
        assertTrue(service.findByListenerIdAndSongId(null, "S1").isEmpty());
        assertTrue(service.findByListenerIdAndSongId("L1", null).isEmpty());
    }

    @Test
    public void testGetRecentHistoryByListenerSuccess() {
        when(repository.findByListener_ListenerIdOrderByPlayedAtDesc("L1")).thenReturn(List.of(history));

        List<HistoryDTO> result = service.getRecentHistoryByListener("L1", 5);

        assertEquals(1, result.size());
    }

    @Test
    public void testGetRecentHistoryByListenerBlankReturnsEmpty() {
        assertTrue(service.getRecentHistoryByListener("", 10).isEmpty());
        assertTrue(service.getRecentHistoryByListener(null, 10).isEmpty());
    }

    @Test
    public void testFindByDateRangeSuccess() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        when(repository.findByPlayedAtBetween(start, end)).thenReturn(List.of(history));

        List<HistoryDTO> result = service.findByDateRange(start, end);

        assertEquals(1, result.size());
    }

    @Test
    public void testFindByDateRangeInvalidThrows() {
        LocalDateTime now = LocalDateTime.now();
        assertThrows(InvalidRequestException.class, () -> service.findByDateRange(null, now));
        assertThrows(InvalidRequestException.class, () -> service.findByDateRange(now, null));
        assertThrows(InvalidRequestException.class, () -> service.findByDateRange(now.plusDays(1), now));
    }

    @Test
    public void testFindByListenerIdAndDateRangeSuccess() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        when(repository.findByListener_ListenerIdAndPlayedAtBetween("L1", start, end)).thenReturn(List.of(history));

        List<HistoryDTO> result = service.findByListenerIdAndDateRange("L1", start, end);

        assertEquals(1, result.size());
    }

    @Test
    public void testFindByListenerIdAndDateRangeInvalidListenerReturnsEmpty() {
        LocalDateTime now = LocalDateTime.now();
        assertTrue(service.findByListenerIdAndDateRange("", now.minusDays(1), now).isEmpty());
        assertTrue(service.findByListenerIdAndDateRange(null, now.minusDays(1), now).isEmpty());
    }

    @Test
    public void testFindByListenerIdAndDateRangeInvalidDatesThrows() {
        LocalDateTime now = LocalDateTime.now();
        assertThrows(InvalidRequestException.class, () -> service.findByListenerIdAndDateRange("L1", null, now));
        assertThrows(InvalidRequestException.class, () -> service.findByListenerIdAndDateRange("L1", now, null));
        assertThrows(InvalidRequestException.class, () -> service.findByListenerIdAndDateRange("L1", now, now.minusDays(1)));
    }

    @Test
    public void testCreateSuccess() {
        HistoryDTO dto = makeDTO();

        when(listenerRepository.findById("L1")).thenReturn(Optional.of(listener));
        when(songRepository.findById("S1")).thenReturn(Optional.of(song));
        when(repository.save(any())).thenReturn(history);

        HistoryDTO result = service.create(dto);

        assertEquals("L1", result.listenerId());
        assertEquals("S1", result.songId());
    }

    @Test
    public void testCreateMissingListenerThrows() {
        HistoryDTO dto = makeDTO();
        
        when(listenerRepository.findById("L1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(dto));
    }

    @Test
    public void testCreateMissingSongThrows() {
        HistoryDTO dto = makeDTO();

        when(listenerRepository.findById("L1")).thenReturn(Optional.of(listener));
        when(songRepository.findById("S1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(dto));
    }   

    @Test
    public void testUpdateSuccess() {
        HistoryDTO dto = makeDTO();

        when(repository.findById(1L)).thenReturn(Optional.of(history));
        when(listenerRepository.findById("L1")).thenReturn(Optional.of(listener));
        when(songRepository.findById("S1")).thenReturn(Optional.of(song));
        when(repository.save(any())).thenReturn(history);

        HistoryDTO result = service.update(1L, dto);

        assertEquals("L1", result.listenerId());
    }

    @Test
    public void testUpdateNotFoundThrows() {
        HistoryDTO dto = makeDTO();

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, dto));
    }

    @Test
    public void testDeleteSuccess() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    public void testDeleteNotFoundThrows() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
    }

    @Test
    public void testDeleteInvalidThrows() {
        assertThrows(InvalidRequestException.class, () -> service.delete(null));
    }
}