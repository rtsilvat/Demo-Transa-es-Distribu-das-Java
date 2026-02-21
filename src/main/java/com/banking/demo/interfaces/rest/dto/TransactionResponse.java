package com.banking.demo.interfaces.rest.dto;

import com.banking.demo.domain.TransactionStatus;
import com.banking.demo.domain.TransactionType;
import com.banking.demo.domain.TransferType;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionResponse {

    private final Long id;
    private final Long accountId;
    private final TransactionType type;
    private final BigDecimal amount;
    private final BigDecimal balanceAfter;
    private final TransferType transferType;
    private final TransactionStatus status;
    private final String correlationId;
    private final Instant createdAt;

    public TransactionResponse(Long id, Long accountId, TransactionType type, BigDecimal amount,
                               BigDecimal balanceAfter, TransferType transferType,
                               TransactionStatus status, String correlationId, Instant createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.transferType = transferType;
        this.status = status;
        this.correlationId = correlationId;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getAccountId() { return accountId; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public TransferType getTransferType() { return transferType; }
    public TransactionStatus getStatus() { return status; }
    public String getCorrelationId() { return correlationId; }
    public Instant getCreatedAt() { return createdAt; }
}
