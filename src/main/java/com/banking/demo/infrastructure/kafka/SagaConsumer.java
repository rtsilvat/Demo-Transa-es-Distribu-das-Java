package com.banking.demo.infrastructure.kafka;

import com.banking.demo.application.dto.DebitCompletedEvent;
import com.banking.demo.application.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SagaConsumer {

    private static final Logger log = LoggerFactory.getLogger(SagaConsumer.class);

    private final TransferService transferService;

    public SagaConsumer(TransferService transferService) {
        this.transferService = transferService;
    }

    @KafkaListener(topics = Topics.SAGA_DEBIT_COMPLETED, groupId = "saga-credit-group")
    public void processDebitCompleted(DebitCompletedEvent event) {
        log.info("Processando DebitCompleted: {}", event.getCorrelationId());
        transferService.processSagaCredit(event);
    }
}
