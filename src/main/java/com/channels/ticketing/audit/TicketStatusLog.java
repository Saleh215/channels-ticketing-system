package com.channels.ticketing.audit;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ticket_status_logs")
public class TicketStatusLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;
    @Column(nullable = false)
    public UUID ticketId;
    public String oldStatus;
    @Column(nullable = false)
    public String newStatus;
    @Column(nullable = false)
    public String changedBy;
    @Column(nullable = false)
    public Instant changedAt;

    protected TicketStatusLog() {
    }

    public TicketStatusLog(UUID ticketId, String oldStatus, String newStatus, String changedBy) {
        this.ticketId = ticketId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.changedAt = Instant.now();
    }
}
