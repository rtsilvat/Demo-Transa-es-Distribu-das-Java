package com.banking.demo.application.port;

import com.banking.demo.domain.Transaction;

import java.util.List;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId, int limit);
}
