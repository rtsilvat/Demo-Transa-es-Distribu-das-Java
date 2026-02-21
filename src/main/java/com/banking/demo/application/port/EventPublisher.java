package com.banking.demo.application.port;

public interface EventPublisher {

    void publish(String topic, String key, Object payload);

    void publishInTransaction(String topic, String key, Object payload);
}
