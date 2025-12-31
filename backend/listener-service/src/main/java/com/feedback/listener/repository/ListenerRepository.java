package com.feedback.listener.repository;

import com.feedback.listener.model.Listener;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListenerRepository extends JpaRepository<Listener, String> {
    // Find listeners by display name
    List<Listener> findByDisplayName(String displayName);

    // Find listeners by partial display name (case-insensitive)
    List<Listener> findByDisplayNameContainingIgnoreCase(String namePart);

    // Find listener by email
    Listener findByEmail(String email);

    // Find listeners by country
    List<Listener> findByCountry(String country);
}