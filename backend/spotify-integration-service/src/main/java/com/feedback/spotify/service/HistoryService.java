package com.feedback.spotify.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import java.util.List;

import com.feedback.spotify.dtos.HistoryDTO;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("history-service")
public interface HistoryService {
    
    @PostMapping("/api/history/create")
	public ResponseEntity<HistoryDTO> create(@RequestBody HistoryDTO historyDTO);

	@GetMapping("/api/history/find-by-listener/{listenerId}")
	public ResponseEntity<List<HistoryDTO>> findByListenerId(@PathVariable String listenerId);

	@GetMapping("/api/history/find-by-listener-and-song/{listenerId}/{songId}")
	public ResponseEntity<List<HistoryDTO>> findByListenerIdAndSongId(@PathVariable String listenerId, @PathVariable String songId);

}