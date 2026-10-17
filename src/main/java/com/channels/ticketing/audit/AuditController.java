package com.channels.ticketing.audit;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
    private final TicketStatusLogRepository tickets;
    private final LoginHistoryLogRepository logins;

    public AuditController(TicketStatusLogRepository tickets, LoginHistoryLogRepository logins) {
        this.tickets = tickets;
        this.logins = logins;
    }

    @GetMapping("/tickets")
    public List<TicketStatusLog> tickets() {
        return tickets.findAll();
    }

    @GetMapping("/logins")
    public List<LoginHistoryLog> logins() {
        return logins.findAll();
    }
}
