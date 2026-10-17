package com.channels.ticketing.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.*;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByStatusAndExpiresAtLessThanEqual(TicketStatus status, Instant time);

    List<Ticket> findByStatusAndExpiredAtLessThanEqual(TicketStatus status, Instant time);
}
