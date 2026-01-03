package com.feedback.spotify.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import com.feedback.spotify.dtos.ArtistDTO;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("artist-service")
public interface ArtistService {
    
    @GetMapping("/api/artists/{id}")
	public ResponseEntity<ArtistDTO> getArtistById(@PathVariable String id);

	@PostMapping("/api/artists")
	public ResponseEntity<ArtistDTO> createArtist(@RequestBody ArtistDTO artistDTO);

	@PutMapping("/api/artists/{id}")
	public ResponseEntity<ArtistDTO> updateArtist(@PathVariable String id, @RequestBody ArtistDTO artistDTO);
}