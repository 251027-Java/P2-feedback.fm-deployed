package com.feedback.song.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class SpotifyApiService {

    // TODO
    // Implement feign client
    
    public Map<String, Object> getCurrentlyPlaying(String accessToken) {
        return null;
    }
    
    public Map<String, Object> getTopTracks(String accessToken, String timeRange) {
        return null;
    }

}

