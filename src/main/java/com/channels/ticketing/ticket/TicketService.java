package com.channels.ticketing.ticket;

import com.channels.ticketing.audit.AuditService;
import com.channels.ticketing.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

@Service
public class TicketService {
    private final TicketRepository tickets;
    private final UserRepository users;
    private final AuditService audit;

    public TicketService(TicketRepository tickets, UserRepository users, AuditService audit) {
        this.tickets = tickets;
        this.users = users;
        this.audit = audit;
    }

    @Transactional
    public Ticket create(String text, String username) {
        var user = users.findByUsername(username).orElseThrow();
        var t = tickets.save(new Ticket(text, user.id, Instant.now()));
        audit.status(t.id, null, "OPEN", username);
        return t;
    }

    @Transactional(readOnly = true)
    public List<Ticket> all() {
        return tickets.findAll();
    }

    @Transactional(readOnly = true)
    public Ticket get(UUID id) {
        return tickets.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
    }

    @Transactional
    public Ticket update(UUID id, String text) {
        var t = get(id);
        requireOpen(t);
        if (!Instant.now().isBefore(t.expiresAt))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ticket time expired");
        t.text = text;
        return t;
    }

    @Transactional
    public Ticket resolve(UUID id, String actor) {
        var t = get(id);
        requireOpen(t);
        if (!Instant.now().isBefore(t.expiresAt))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ticket time expired");
        t.status = TicketStatus.RESOLVED;
        audit.status(t.id, "OPEN", "RESOLVED", actor);
        return t;
    }

    private void requireOpen(Ticket t) {
        if (t.status != TicketStatus.OPEN) throw new ResponseStatusException(HttpStatus.CONFLICT, "Ticket is not open");
    }

    @Transactional
    public void expire() {
        Instant now = Instant.now();
        for (var t : tickets.findByStatusAndExpiresAtLessThanEqual(TicketStatus.OPEN, now)) {
            t.status = TicketStatus.EXPIRED;
            t.expiredAt = now;
            audit.status(t.id, "OPEN", "EXPIRED", "SYSTEM");
        }
    }

    @Transactional
    public void remove() {
        Instant cutoff = Instant.now().minusSeconds(180);
        for (var t : tickets.findByStatusAndExpiredAtLessThanEqual(TicketStatus.EXPIRED, cutoff)) {
            audit.status(t.id, "EXPIRED", "DELETED", "SYSTEM");
            tickets.delete(t);
        }
    }
}
