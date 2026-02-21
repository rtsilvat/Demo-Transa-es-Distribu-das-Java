package com.banking.demo.infrastructure.kafka;

import com.banking.demo.application.port.EventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class KafkaEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Retryable(retryFor = Exception.class, maxAttempts = 3)
    public void publish(String topic, String key, Object payload) {
        try {
            kafkaTemplate.send(topic, key, payload).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("Falha ao publicar no Kafka", e);
        }
    }

    @Override
    public void publishInTransaction(String topic, String key, Object payload) {
        kafkaTemplate.executeInTransaction(ops -> {
            try {
                ops.send(topic, key, payload).get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException("Falha na transação Kafka", e);
            }
            return true;
        });
    }

    @Recover
    public void recover(Exception e, String topic, String key, Object payload) {
        throw new RuntimeException("Falha ao publicar evento no Kafka após retentativas: " + topic, e);
    }
}
