package com.banking.demo.interfaces.rest.dto;

import java.time.Instant;

public class TransferLogResponse {

    private final Integer step;
    private final String message;
    private final Instant createdAt;

    public TransferLogResponse(Integer step, String message, Instant createdAt) {
        this.step = step;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Integer getStep() { return step; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }
}
