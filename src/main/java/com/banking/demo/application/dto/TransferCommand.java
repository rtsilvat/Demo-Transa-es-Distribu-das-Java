package com.banking.demo.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class TransferCommand {

    private final Long fromAccountId;
    private final Long toAccountId;
    private final BigDecimal amount;
    private final String correlationId;
    private final boolean forceError;

    @JsonCreator
    public TransferCommand(@JsonProperty("fromAccountId") Long fromAccountId,
                           @JsonProperty("toAccountId") Long toAccountId,
                           @JsonProperty("amount") BigDecimal amount,
                           @JsonProperty("correlationId") String correlationId,
                           @JsonProperty("forceError") boolean forceError) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.correlationId = correlationId;
        this.forceError = forceError;
    }

    public Long getFromAccountId() { return fromAccountId; }
    public Long getToAccountId() { return toAccountId; }
    public BigDecimal getAmount() { return amount; }
    public String getCorrelationId() { return correlationId; }
    public boolean isForceError() { return forceError; }
}
