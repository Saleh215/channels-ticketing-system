package com.channels.ticketing.ticket;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TicketScheduler {
    private final TicketService service;

    public TicketScheduler(TicketService service) {
        this.service = service;
    }

    @Scheduled(fixedDelay = 1000)
    public void expire() {
        service.expire();
    }

    @Scheduled(fixedDelay = 1000)
    public void remove() {
        service.remove();
    }
}
