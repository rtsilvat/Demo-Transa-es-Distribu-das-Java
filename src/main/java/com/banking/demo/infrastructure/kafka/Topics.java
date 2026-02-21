package com.banking.demo.infrastructure.kafka;

public final class Topics {

    public static final String KAFKA_NATIVE_TRANSFER = "transfer.kafka-native";
    public static final String SAGA_DEBIT_COMPLETED = "saga.debit.completed";
    public static final String SAGA_COMPENSATION_REQUIRED = "saga.compensation.required";
    public static final String OUTBOX_TRANSFER = "outbox.transfer";

    private Topics() {
    }
}
