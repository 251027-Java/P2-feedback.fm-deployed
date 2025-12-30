package com.feedback.artist.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "artists")
@Data
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
