package com.feedback.spotify.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import com.feedback.spotify.dtos.SongDTO;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("song-service")
public interface SongService {
    
    @GetMapping("/api/songs/{id}")
	public ResponseEntity<SongDTO> getSongById(@PathVariable String id);

    @PostMapping("/api/songs")
	public ResponseEntity<SongDTO> createSong(@RequestBody SongDTO songDTO);
     
	@PutMapping("/api/songs/{id}")
	public ResponseEntity<SongDTO> updateSong(@PathVariable String id, @RequestBody SongDTO songDTO);
}