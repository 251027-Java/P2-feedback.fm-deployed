package com.feedback.history.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.feedback.history.dtos.SongDTO;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("song-service")
public interface SongService {
    
    @GetMapping("/find-by-id/{id}")
    public SongDTO findById(@PathVariable String id);    

}