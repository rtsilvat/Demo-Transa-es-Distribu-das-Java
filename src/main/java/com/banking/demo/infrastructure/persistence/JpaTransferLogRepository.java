package com.banking.demo.infrastructure.persistence;

import com.banking.demo.application.port.TransferLogRepository;
import com.banking.demo.domain.TransferLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaTransferLogRepository extends JpaRepository<TransferLog, Long>, TransferLogRepository {

    List<TransferLog> findByCorrelationIdOrderByStepAsc(String correlationId);

    boolean existsByCorrelationIdAndStep(String correlationId, int step);
}