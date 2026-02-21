package com.banking.demo.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "transfer_log")
public class TransferLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correlation_id", nullable = false, length = 100)
    private String correlationId;

    @Column(nullable = false)
    private Integer step;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "transfer_type", length = 20)
    private String transferType;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected TransferLog() {
    }

    public TransferLog(String correlationId, int step, String message, String transferType) {
        this.correlationId = correlationId;
        this.step = step;
        this.message = message;
        this.transferType = transferType;
    }

    public Long getId() {
        return id;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public Integer getStep() {
        return step;
    }

    public String getMessage() {
        return message;
    }

    public String getTransferType() {
        return transferType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransferLog that = (TransferLog) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
