package com.channels.ticketing.ticket;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;
    @Column(nullable = false, length = 250)
    public String text;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TicketStatus status = TicketStatus.OPEN;
    @Column(nullable = false)
    public UUID createdBy;
    @Column(nullable = false)
    public Instant createdAt;
    @Column(nullable = false)
    public Instant expiresAt;
    public Instant expiredAt;
    @Version
    public long version;

    protected Ticket() {
    }

    public Ticket(String text, UUID createdBy, Instant now) {
        this.text = text;
        this.createdBy = createdBy;
        this.createdAt = now;
        this.expiresAt = now.plusSeconds(30);
    }
}
