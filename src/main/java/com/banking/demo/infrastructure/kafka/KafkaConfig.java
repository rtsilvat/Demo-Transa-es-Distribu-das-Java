package com.banking.demo.infrastructure.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic kafkaNativeTransferTopic() {
        return TopicBuilder.name(Topics.KAFKA_NATIVE_TRANSFER).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic sagaDebitCompletedTopic() {
        return TopicBuilder.name(Topics.SAGA_DEBIT_COMPLETED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic sagaCompensationRequiredTopic() {
        return TopicBuilder.name(Topics.SAGA_COMPENSATION_REQUIRED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic outboxTransferTopic() {
        return TopicBuilder.name(Topics.OUTBOX_TRANSFER).partitions(3).replicas(1).build();
    }
}
