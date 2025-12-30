package com.feedback.artist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ArtistApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArtistApplication.class, args);
    }
}
