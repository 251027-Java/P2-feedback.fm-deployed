package com.feedback.history.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "history_records")
@Data
@NoArgsConstructor
public class HistoryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String listenerId;

    private String songId;

    private Instant playedAt;

    private Integer durationMs;
}
