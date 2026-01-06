package com.feedback.logging_service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
public class FileLogWriter {

    private static final Path LOG_DIR = Paths.get("logs");

    public FileLogWriter() throws IOException {
        Files.createDirectories(LOG_DIR);
    }

    public synchronized void write(String topic, String message) {
        try {
            String serviceName = topic.replace("logs.", "");
            Path logFile = LOG_DIR.resolve(serviceName + ".log");

            String entry = Instant.now() + " | " + message + System.lineSeparator();
            Files.writeString(
                    logFile,
                    entry,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            e.printStackTrace(); // demo-level error handling
        }
    }
}