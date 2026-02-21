package com.banking.demo.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class CompensationRequiredEvent {

    private final Long fromAccountId;
    private final Long toAccountId;
    private final BigDecimal amount;
    private final String correlationId;

    @JsonCreator
    public CompensationRequiredEvent(@JsonProperty("fromAccountId") Long fromAccountId,
                                     @JsonProperty("toAccountId") Long toAccountId,
                                     @JsonProperty("amount") BigDecimal amount,
                                     @JsonProperty("correlationId") String correlationId) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.correlationId = correlationId;
    }

    public Long getFromAccountId() { return fromAccountId; }
    public Long getToAccountId() { return toAccountId; }
    public BigDecimal getAmount() { return amount; }
    public String getCorrelationId() { return correlationId; }
}
