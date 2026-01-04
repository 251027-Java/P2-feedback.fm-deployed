package com.feedback.logging_service;

import lombok.Data;

@Data
public class LogEvent {
    private String message;
    private String level;
    private String service;
    private String environment;
    private String traceId;
    private String logger_name;
    private String thread_name;
    private String timestamp;
}
