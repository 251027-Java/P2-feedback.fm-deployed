package com.feedback.spotify.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import com.feedback.spotify.dtos.AlbumDTO;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("album-service")
public interface AlbumService {
    
    @GetMapping("/api/albums/{id}")
	public ResponseEntity<AlbumDTO> getAlbumById(@PathVariable String id);

	@PostMapping("/api/albums")
	public ResponseEntity<AlbumDTO> createAlbum(@RequestBody AlbumDTO albumDTO);

	@PutMapping("/api/albums/{id}")
	public ResponseEntity<AlbumDTO> updateAlbum(@PathVariable String id, @RequestBody AlbumDTO albumDTO);
}