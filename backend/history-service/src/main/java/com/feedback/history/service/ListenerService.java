package com.feedback.history.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.feedback.history.dtos.ListenerDTO;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("listener-service")
public interface ListenerService {
    
    @GetMapping("/find-by-id/{id}")
    public ListenerDTO findById(@PathVariable String id);   

}