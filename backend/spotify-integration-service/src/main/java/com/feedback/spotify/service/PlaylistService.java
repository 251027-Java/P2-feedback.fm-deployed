package com.feedback.spotify.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import com.feedback.spotify.dtos.PlaylistDTO;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("playlist-service")
public interface PlaylistService {
    
    @GetMapping("/api/playlists/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable String id);

    @PostMapping("/api/playlists")
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestBody PlaylistDTO playlistDTO);

    @PutMapping("/api/playlists/{id}")
    public ResponseEntity<PlaylistDTO> updatePlaylist(@PathVariable String id, @RequestBody PlaylistDTO playlistDTO);
}