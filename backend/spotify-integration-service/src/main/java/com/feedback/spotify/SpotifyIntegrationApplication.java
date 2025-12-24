package com.feedback.spotify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SpotifyIntegrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpotifyIntegrationApplication.class, args);
    }
}
