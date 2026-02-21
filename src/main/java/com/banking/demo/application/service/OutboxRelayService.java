package com.banking.demo.application.service;

import com.banking.demo.application.port.OutboxRepository;
import com.banking.demo.domain.OutboxEvent;
import com.banking.demo.infrastructure.kafka.Topics;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class OutboxRelayService {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelayService.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxRelayService(OutboxRepository outboxRepository,
                              KafkaTemplate<String, Object> kafkaTemplate,
                              ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void relayUnpublished() {
        List<OutboxEvent> events = outboxRepository.findUnpublished(10);
        for (OutboxEvent event : events) {
            try {
                Object payload = objectMapper.readValue(event.getPayload(), Map.class);
                kafkaTemplate.send(Topics.OUTBOX_TRANSFER, event.getAggregateId(), payload).get();
                event.markAsPublished();
                outboxRepository.save(event);
                log.debug("Evento outbox publicado: {}", event.getId());
            } catch (Exception e) {
                log.error("Erro ao publicar evento outbox {}: {}", event.getId(), e.getMessage());
            }
        }
    }
}
