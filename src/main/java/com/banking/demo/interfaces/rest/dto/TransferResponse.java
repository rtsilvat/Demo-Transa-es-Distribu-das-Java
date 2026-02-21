package com.banking.demo.interfaces.rest.dto;

public class TransferResponse {

    private final String correlationId;

    public TransferResponse(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getCorrelationId() { return correlationId; }
}
