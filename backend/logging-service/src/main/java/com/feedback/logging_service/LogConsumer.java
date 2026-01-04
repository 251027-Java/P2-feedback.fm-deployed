package com.feedback.logging_service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class LogConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(LogConsumer.class);

    private static final Path BASE_LOG_DIR = Paths.get("/logs");

    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "logs.raw", containerFactory = "kafkaListenerContainerFactory")
    public void consume(
            String payload,
            Acknowledgment ack) {

        try {
            LogEvent event = mapper.readValue(payload, LogEvent.class);

            save(event);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process log event", e);
        }
    }

    private void save(LogEvent event) throws IOException {

        String serviceName =
                Optional.ofNullable(event.getService())
                        .orElse("unknown-service");

        Path serviceDir = BASE_LOG_DIR.resolve(serviceName);
        Files.createDirectories(serviceDir);

        Path logFile = serviceDir.resolve("app.log");

        String logLine = format(event);

        Files.writeString(
                logFile,
                logLine,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    private String format(LogEvent event) {
        return String.format(
                "%s [%s] [%s] %s - %s%n",
                event.getTimestamp(),
                event.getLevel(),
                event.getTraceId(),
                event.getLogger_name(),
                event.getMessage()
        );
    }
}

