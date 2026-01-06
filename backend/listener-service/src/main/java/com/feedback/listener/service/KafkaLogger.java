package com.feedback.listener.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaLogger {

    private static final String TOPIC = "logs.listener-service";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaLogger(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void log(String message) {
        kafkaTemplate.send(TOPIC, message);
    }
}