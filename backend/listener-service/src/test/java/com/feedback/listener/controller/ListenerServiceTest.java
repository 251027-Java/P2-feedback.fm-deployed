package com.feedback.listener.controller;

import com.feedback.listener.controller.ListenerController;
import com.feedback.listener.model.Listener;
import com.feedback.listener.model.ListenerStats;
import com.feedback.listener.repository.ListenerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ListenerServiceTest {

    // @Mock
    // private ListenerRepository repository;

    // private ListenerController controller;

    // @BeforeEach
    // public void setUp() {
    //     MockitoAnnotations.openMocks(this);
    //     controller = new ListenerController(repository);
    // }

    // @Test
    // public void getUserReturnsListenerWhenFound() {
    //     Listener l = new Listener();
    //     l.setListenerId("L1");
    //     l.setDisplayName("Alice");

    //     when(repository.findById("L1")).thenReturn(Optional.of(l));

    //     ResponseEntity<Listener> resp = controller.getUser("L1");

    //     assertEquals(200, resp.getStatusCodeValue());
    //     assertEquals("L1", resp.getBody().getListenerId());
    // }

    // @Test
    // public void getUserReturnsNotFoundWhenMissing() {
    //     when(repository.findById("MISSING")).thenReturn(Optional.empty());

    //     ResponseEntity<Listener> resp = controller.getUser("MISSING");

    //     assertEquals(404, resp.getStatusCodeValue());
    // }

    // @Test
    // public void registerReturnsBadRequestForInvalidId() {
    //     Listener l = new Listener();
    //     l.setListenerId("");

    //     ResponseEntity<Listener> resp = controller.register(l);

    //     assertEquals(400, resp.getStatusCodeValue());
    // }

    // @Test
    // public void registerReturnsCreatedAndLocation() {
    //     Listener l = new Listener();
    //     l.setListenerId("L2");
    //     l.setDisplayName("Bob");

    //     when(repository.save(l)).thenReturn(l);

    //     ResponseEntity<Listener> resp = controller.register(l);

    //     assertEquals(201, resp.getStatusCodeValue());
    //     assertEquals("L2", resp.getBody().getListenerId());
    //     assertEquals(java.net.URI.create("/api/users/L2"), resp.getHeaders().getLocation());
    // }

    // @Test
    // public void loginReturnsBadRequestForMissingEmail() {
    //     ResponseEntity<Map<String, Object>> resp = controller.login(Map.of());
    //     assertEquals(400, resp.getStatusCodeValue());
    // }

    // @Test
    // public void loginReturnsUnauthorizedWhenNotFound() {
    //     when(repository.findByEmail("nope@example.com")).thenReturn(Optional.empty());

    //     ResponseEntity<Map<String, Object>> resp = controller.login(Map.of("email", "nope@example.com"));

    //     assertEquals(401, resp.getStatusCodeValue());
    //     assertTrue(resp.getBody().containsKey("error"));
    // }

    // @Test
    // public void loginReturnsOkWithTokenWhenFound() {
    //     Listener l = new Listener();
    //     l.setListenerId("L3");
    //     l.setEmail("me@example.com");

    //     when(repository.findByEmail("me@example.com")).thenReturn(Optional.of(l));

    //     ResponseEntity<Map<String, Object>> resp = controller.login(Map.of("email", "me@example.com"));

    //     assertEquals(200, resp.getStatusCodeValue());
    //     assertEquals("L3", resp.getBody().get("listenerId"));
    //     assertEquals("me@example.com", resp.getBody().get("email"));
    //     assertTrue(resp.getBody().containsKey("token"));
    // }

    // @Test
    // public void updateUserUpdatesExistingListener() {
    //     Listener existing = new Listener();
    //     existing.setListenerId("L4");
    //     existing.setDisplayName("Old");

    //     Listener update = new Listener();
    //     update.setDisplayName("New");
    //     update.setEmail("new@example.com");
    //     update.setCountry("USA");
    //     update.setHref("href");

    //     when(repository.findById("L4")).thenReturn(Optional.of(existing));
    //     when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

    //     ResponseEntity<Listener> resp = controller.updateUser("L4", update);

    //     assertEquals(200, resp.getStatusCodeValue());
    //     assertEquals("New", resp.getBody().getDisplayName());
    // }

    // @Test
    // public void updateUserReturnsNotFoundWhenMissing() {
    //     Listener update = new Listener();
    //     when(repository.findById("X")).thenReturn(Optional.empty());

    //     ResponseEntity<Listener> resp = controller.updateUser("X", update);

    //     assertEquals(404, resp.getStatusCodeValue());
    // }

    // @Test
    // public void getDashboardFallsBackToListenerFieldsWhenNoStats() {
    //     Listener l = new Listener();
    //     l.setListenerId("L5");
    //     l.setDisplayName("Dana");
    //     l.setEmail("dana@example.com");
    //     l.setTotalListeningTimeMs(12345L);
    //     l.setTotalSongsPlayed(7);

    //     when(repository.findById("L5")).thenReturn(Optional.of(l));

    //     ResponseEntity<Map<String, Object>> resp = controller.getDashboard("L5");

    //     assertEquals(200, resp.getStatusCodeValue());
    //     Map<String, Object> body = resp.getBody();
    //     assertEquals("L5", body.get("userId"));
    //     assertEquals("Dana", body.get("displayName"));
    //     assertEquals("dana@example.com", body.get("email"));
    //     Object statsObj = body.get("stats");
    //     assertTrue(statsObj instanceof ListenerStats);
    //     ListenerStats stats = (ListenerStats) statsObj;
    //     assertEquals(12345L, stats.getTotalListeningTimeMs());
    //     assertEquals(7, stats.getTotalSongsPlayed());
    // }
}
