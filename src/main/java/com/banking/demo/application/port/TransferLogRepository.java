package com.banking.demo.application.port;

import com.banking.demo.domain.TransferLog;

import java.util.List;

public interface TransferLogRepository {

    TransferLog save(TransferLog log);

    List<TransferLog> findByCorrelationIdOrderByStepAsc(String correlationId);

    boolean existsByCorrelationIdAndStep(String correlationId, int step);
}
