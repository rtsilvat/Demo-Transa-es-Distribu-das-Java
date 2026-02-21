package com.banking.demo.infrastructure.scheduler;

import com.banking.demo.application.service.OutboxRelayService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxRelayScheduler {

    private final OutboxRelayService outboxRelayService;

    public OutboxRelayScheduler(OutboxRelayService outboxRelayService) {
        this.outboxRelayService = outboxRelayService;
    }

    @Scheduled(fixedDelay = 2000)
    public void relayOutbox() {
        outboxRelayService.relayUnpublished();
    }
}
