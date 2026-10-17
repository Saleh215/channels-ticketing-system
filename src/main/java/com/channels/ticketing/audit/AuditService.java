package com.channels.ticketing.audit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuditService {
    private final TicketStatusLogRepository ticketLogs;
    private final LoginHistoryLogRepository loginLogs;

    public AuditService(TicketStatusLogRepository ticketLogs, LoginHistoryLogRepository loginLogs) {
        this.ticketLogs = ticketLogs;
        this.loginLogs = loginLogs;
    }

    public void status(UUID id, String before, String after, String actor) {
        ticketLogs.save(new TicketStatusLog(id, before, after, actor));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void login(String username, boolean success) {
        loginLogs.save(new LoginHistoryLog(username, success));
    }
}
