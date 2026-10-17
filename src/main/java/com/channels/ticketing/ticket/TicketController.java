package com.channels.ticketing.ticket;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    public record TicketRequest(
            @NotBlank @Size(max = 250) @Pattern(regexp = "^[\\p{L} ]+$", message = "Only letters and spaces are allowed") String text) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ticket create(@Valid @RequestBody TicketRequest req, Authentication auth) {
        return service.create(req.text(), auth.getName());
    }

    @GetMapping
    public List<Ticket> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public Ticket get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PatchMapping("/{id}")
    public Ticket update(@PathVariable UUID id, @Valid @RequestBody TicketRequest req) {
        return service.update(id, req.text());
    }

    @PatchMapping("/{id}/resolve")
    public Ticket resolve(@PathVariable UUID id, Authentication auth) {
        return service.resolve(id, auth.getName());
    }
}
