package com.feedback.history.controller;

import com.feedback.history.controller.HistoryController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HistoryServiceTest {

    // @Mock
    // private HistoryRecordRepository repository;

    // private HistoryController controller;

    // @BeforeEach
    // public void setUp() {
    //     MockitoAnnotations.openMocks(this);
    //     controller = new HistoryController(repository);
    // }

    // @Test
    // public void listReturnsRecordsForListener() {
    //     HistoryRecord r = new HistoryRecord();
    //     r.setId(1L);
    //     r.setListenerId("L1");
    //     r.setSongId("S1");
    //     r.setPlayedAt(Instant.now().minusSeconds(3600));

    //     when(repository.findByListenerIdOrderByPlayedAtDesc("L1")).thenReturn(List.of(r));

    //     List<HistoryRecord> result = controller.list("L1");

    //     assertEquals(1, result.size());
    //     assertEquals("L1", result.get(0).getListenerId());
    // }

    // @Test
    // public void addSetsListenerAndPlayedAtAndReturnsCreated() {
    //     HistoryRecord input = new HistoryRecord();
    //     input.setSongId("S1");
    //     input.setDurationMs(180000);

    //     when(repository.save(any())).thenAnswer(invocation -> {
    //         HistoryRecord saved = invocation.getArgument(0);
    //         saved.setId(42L);
    //         return saved;
    //     });

    //     ResponseEntity<HistoryRecord> resp = controller.add("L1", input);

    //     assertEquals(201, resp.getStatusCodeValue());
    //     assertEquals(URI.create("/api/history/L1/42"), resp.getHeaders().getLocation());
    //     HistoryRecord body = resp.getBody();
    //     assertNotNull(body);
    //     assertEquals(42L, body.getId());
    //     assertEquals("L1", body.getListenerId());
    //     assertNotNull(body.getPlayedAt());
    // }

    // @Test
    // public void listReturnsEmptyWhenNoRecords() {
    //     when(repository.findByListenerIdOrderByPlayedAtDesc("L2")).thenReturn(List.of());

    //     List<HistoryRecord> result = controller.list("L2");

    //     assertNotNull(result);
    //     assertTrue(result.isEmpty());
    // }

    // @Test
    // public void addPreservesProvidedPlayedAt() {
    //     HistoryRecord input = new HistoryRecord();
    //     input.setSongId("S2");
    //     Instant provided = Instant.parse("2020-01-01T12:00:00Z");
    //     input.setPlayedAt(provided);

    //     when(repository.save(any())).thenAnswer(invocation -> {
    //         HistoryRecord saved = invocation.getArgument(0);
    //         saved.setId(7L);
    //         return saved;
    //     });

    //     ResponseEntity<HistoryRecord> resp = controller.add("L1", input);

    //     assertEquals(201, resp.getStatusCodeValue());
    //     assertEquals(URI.create("/api/history/L1/7"), resp.getHeaders().getLocation());
    //     HistoryRecord body = resp.getBody();
    //     assertNotNull(body);
    //     assertEquals(provided, body.getPlayedAt());
    // }
}
