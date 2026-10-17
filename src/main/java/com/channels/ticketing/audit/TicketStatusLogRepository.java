package com.channels.ticketing.audit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface TicketStatusLogRepository extends JpaRepository<TicketStatusLog,UUID> {}
