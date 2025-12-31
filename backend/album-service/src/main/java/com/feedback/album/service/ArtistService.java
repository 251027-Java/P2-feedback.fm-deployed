package com.feedback.album.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import com.feedback.album.dtos.ArtistDTO;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("artist-service")
public interface ArtistService {
    
    @GetMapping("/api/artist/find-by-id/{id}")
    public ArtistDTO findById(@PathVariable String id);   

    @GetMapping("/api/artist/exists/{id}")
    public boolean existsById(@PathVariable String id);
}

