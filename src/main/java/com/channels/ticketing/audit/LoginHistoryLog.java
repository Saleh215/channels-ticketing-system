package com.channels.ticketing.audit;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "login_history_logs")
public class LoginHistoryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;
    @Column(nullable = false)
    public String username;
    @Column(nullable = false)
    public boolean success;
    @Column(nullable = false)
    public Instant attemptedAt;

    protected LoginHistoryLog() {
    }

    public LoginHistoryLog(String username, boolean success) {
        this.username = username;
        this.success = success;
        this.attemptedAt = Instant.now();
    }
}
