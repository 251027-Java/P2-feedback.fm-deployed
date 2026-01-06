package com.feedback.album.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaLogger {

    private static final String TOPIC = "logs.album-service";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaLogger(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void log(String message) {
        kafkaTemplate.send(TOPIC, message);
    }
}