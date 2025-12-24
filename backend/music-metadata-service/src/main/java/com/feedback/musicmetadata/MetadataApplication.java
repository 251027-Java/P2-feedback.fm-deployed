package com.feedback.musicmetadata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MetadataApplication {
    public static void main(String[] args) {
        SpringApplication.run(MetadataApplication.class, args);
    }
}
