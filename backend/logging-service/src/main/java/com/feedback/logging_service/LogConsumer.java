package com.feedback.logging_service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class LogConsumer {

    private final FileLogWriter fileLogWriter;

    public LogConsumer(FileLogWriter fileLogWriter) {
        this.fileLogWriter = fileLogWriter;
    }

    @KafkaListener(
        topicPattern = "logs\\..*",
        groupId = "logger-service"
    )
    public void consume(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic
    ) {
        fileLogWriter.write(topic, message);
    }
}