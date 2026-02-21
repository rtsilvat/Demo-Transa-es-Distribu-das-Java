package com.banking.demo.infrastructure.kafka;

import com.banking.demo.application.dto.CompensationRequiredEvent;
import com.banking.demo.application.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CompensationConsumer {

    private static final Logger log = LoggerFactory.getLogger(CompensationConsumer.class);

    private final TransferService transferService;

    public CompensationConsumer(TransferService transferService) {
        this.transferService = transferService;
    }

    @KafkaListener(topics = Topics.SAGA_COMPENSATION_REQUIRED, groupId = "saga-compensation-group")
    public void processCompensation(CompensationRequiredEvent event) {
        log.info("Processando compensação: {}", event.getCorrelationId());
        transferService.processCompensation(event);
    }
}
