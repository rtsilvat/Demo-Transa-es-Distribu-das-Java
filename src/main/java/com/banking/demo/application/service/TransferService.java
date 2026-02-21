package com.banking.demo.application.service;

import com.banking.demo.application.dto.CompensationRequiredEvent;
import com.banking.demo.application.dto.DebitCompletedEvent;
import com.banking.demo.application.dto.TransferCommand;
import com.banking.demo.application.port.EventPublisher;
import com.banking.demo.application.port.OutboxRepository;
import com.banking.demo.application.port.TransferLogRepository;
import com.banking.demo.domain.OutboxEvent;
import com.banking.demo.domain.TransferLog;
import com.banking.demo.domain.TransferType;
import com.banking.demo.infrastructure.kafka.Topics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);
    private static final BigDecimal SAGA_LIMIT = new BigDecimal("10000");

    private final AccountService accountService;
    private final EventPublisher eventPublisher;
    private final OutboxRepository outboxRepository;
    private final TransferLogRepository transferLogRepository;
    private final ObjectMapper objectMapper;

    public TransferService(AccountService accountService,
                           EventPublisher eventPublisher,
                           OutboxRepository outboxRepository,
                           TransferLogRepository transferLogRepository,
                           ObjectMapper objectMapper) {
        this.accountService = accountService;
        this.eventPublisher = eventPublisher;
        this.outboxRepository = outboxRepository;
        this.transferLogRepository = transferLogRepository;
        this.objectMapper = objectMapper;
    }

    public String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    public List<TransferLog> getLogs(String correlationId) {
        return transferLogRepository.findByCorrelationIdOrderByStepAsc(correlationId);
    }

    // --- Fluxo 1: Kafka Native ---
    @Transactional(rollbackFor = Exception.class)
    public String transferKafkaNative(Long fromAccountId, Long toAccountId, BigDecimal amount, boolean forceError) {
        String correlationId = generateCorrelationId();
        accountService.logStep(correlationId, 1, "Iniciando transferência Kafka Native", "KAFKA_NATIVE");

        TransferCommand command = new TransferCommand(fromAccountId, toAccountId, amount, correlationId, forceError);
        eventPublisher.publishInTransaction(Topics.KAFKA_NATIVE_TRANSFER, correlationId, command);
        accountService.logStep(correlationId, 2, "Evento publicado no Kafka (executeInTransaction)", "KAFKA_NATIVE");

        return correlationId;
    }

    // --- Fluxo 2: Saga ---
    @Transactional(rollbackFor = Exception.class)
    public String transferSaga(Long fromAccountId, Long toAccountId, BigDecimal amount, boolean forceError) {
        String correlationId = generateCorrelationId();
        accountService.logStep(correlationId, 1, "Débito realizado na conta origem", "SAGA");

        accountService.debit(fromAccountId, amount, correlationId, TransferType.SAGA);
        accountService.logStep(correlationId, 2, "Evento DebitCompleted publicado", "SAGA");

        DebitCompletedEvent event = new DebitCompletedEvent(fromAccountId, toAccountId, amount, correlationId, forceError);
        eventPublisher.publish(Topics.SAGA_DEBIT_COMPLETED, correlationId, event);

        return correlationId;
    }

    // Chamado pelo SagaConsumer
    @Transactional
    public void processSagaCredit(DebitCompletedEvent event) {
        String correlationId = event.getCorrelationId();
        accountService.logStep(correlationId, 3, "Processando crédito na conta destino", "SAGA");

        boolean shouldFail = event.isForceError() || event.getAmount().compareTo(SAGA_LIMIT) > 0;
        if (shouldFail) {
            accountService.logStep(correlationId, 4, "Erro de conformidade (valor > 10.000 ou forceError). Disparando compensação.", "SAGA");
            CompensationRequiredEvent compEvent = new CompensationRequiredEvent(
                    event.getFromAccountId(), event.getToAccountId(), event.getAmount(), correlationId);
            eventPublisher.publish(Topics.SAGA_COMPENSATION_REQUIRED, correlationId, compEvent);
            return;
        }

        accountService.credit(event.getToAccountId(), event.getAmount(), correlationId, TransferType.SAGA);
        accountService.logStep(correlationId, 5, "Crédito realizado com sucesso", "SAGA");
    }

    // Chamado pelo CompensationConsumer - idempotente: ignora se já compensou
    @Transactional
    public void processCompensation(CompensationRequiredEvent event) {
        String correlationId = event.getCorrelationId();
        if (transferLogRepository.existsByCorrelationIdAndStep(correlationId, 7)) {
            return;
        }
        accountService.logStep(correlationId, 6, "Compensação: estornando débito na conta origem", "SAGA");
        accountService.refund(event.getFromAccountId(), event.getAmount(), correlationId, TransferType.SAGA);
        accountService.logStep(correlationId, 7, "Compensação concluída", "SAGA");
    }

    // --- Fluxo 3: Outbox ---
    @Transactional(rollbackFor = Exception.class)
    public String transferOutbox(Long fromAccountId, Long toAccountId, BigDecimal amount, boolean forceError) {
        String correlationId = generateCorrelationId();
        accountService.logStep(correlationId, 1, "Iniciando transferência Outbox", "OUTBOX");

        if (forceError || amount.compareTo(SAGA_LIMIT) > 0) {
            throw new IllegalStateException("Transferência rejeitada por conformidade (valor > 10.000 ou Forçar erro) - Transação revertida");
        }

        accountService.debit(fromAccountId, amount, correlationId, TransferType.OUTBOX);
        accountService.credit(toAccountId, amount, correlationId, TransferType.OUTBOX);

        // Persistir outbox na mesma transação
        String payload;
        try {
            payload = objectMapper.writeValueAsString(new TransferCommand(fromAccountId, toAccountId, amount, correlationId, false));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao serializar evento", e);
        }
        OutboxEvent outboxEvent = new OutboxEvent("Transfer", correlationId, "TransferCompleted", payload);
        outboxRepository.save(outboxEvent);

        accountService.logStep(correlationId, 2, "Débito, crédito e outbox persistidos na mesma transação", "OUTBOX");

        return correlationId;
    }
}
