package com.banking.demo.infrastructure.persistence;

import com.banking.demo.application.port.TransactionRepository;
import com.banking.demo.domain.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaTransactionRepository extends JpaRepository<Transaction, Long>, TransactionRepository {

    List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    @Override
    default List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId, int limit) {
        return findByAccountIdOrderByCreatedAtDesc(accountId, Pageable.ofSize(limit));
    }
}
