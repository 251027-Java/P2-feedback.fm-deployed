package com.feedback.spotify.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import com.feedback.spotify.dtos.ListenerDTO;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("listener-service")
public interface ListenerService {
    
    @GetMapping("/api/users/find-by-id/{id}")
    public ListenerDTO findById(@PathVariable String id);  

    @PutMapping("/api/users/{id}")
    public ResponseEntity<ListenerDTO> updateUser(@PathVariable String id, @RequestBody ListenerDTO listenerDTO);

    @PostMapping("/api/users/register")
    public ResponseEntity<ListenerDTO> register(@RequestBody ListenerDTO listenerDTO); 

}