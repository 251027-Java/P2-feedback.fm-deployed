package com.feedback.playlist.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.feedback.playlist.dtos.ListenerDTO;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("listener-service")
public interface ListenerService {
    
    @GetMapping("/find-by-id/{id}")
    public ListenerDTO findById(@PathVariable String id);   

    @GetMapping("/exists/{id}")
    public boolean existsById(@PathVariable String id);
}