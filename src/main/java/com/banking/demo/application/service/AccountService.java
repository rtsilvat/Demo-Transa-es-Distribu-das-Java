package com.banking.demo.application.service;

import com.banking.demo.application.port.AccountRepository;
import com.banking.demo.application.port.TransactionRepository;
import com.banking.demo.application.port.TransferLogRepository;
import com.banking.demo.domain.Account;
import com.banking.demo.domain.Transaction;
import com.banking.demo.domain.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransferLogRepository transferLogRepository;

    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository,
                          TransferLogRepository transferLogRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transferLogRepository = transferLogRepository;
    }

    @Transactional(readOnly = true)
    public List<Account> listAll() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Account getById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<Transaction> getStatement(Long accountId, int limit) {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId, limit);
    }

    @Transactional
    public void debit(Long accountId, BigDecimal amount, String correlationId,
                      com.banking.demo.domain.TransferType transferType) {
        Account account = getById(accountId);
        account.debit(amount);
        accountRepository.save(account);

        Transaction tx = new Transaction(accountId, TransactionType.DEBIT, amount,
                account.getBalance(), transferType, correlationId);
        transactionRepository.save(tx);
    }

    @Transactional
    public void credit(Long accountId, BigDecimal amount, String correlationId,
                       com.banking.demo.domain.TransferType transferType) {
        Account account = getById(accountId);
        account.credit(amount);
        accountRepository.save(account);

        Transaction tx = new Transaction(accountId, TransactionType.CREDIT, amount,
                account.getBalance(), transferType, correlationId);
        transactionRepository.save(tx);
    }

    @Transactional
    public void refund(Long accountId, BigDecimal amount, String correlationId,
                       com.banking.demo.domain.TransferType transferType) {
        credit(accountId, amount, correlationId, transferType);
    }

    public void logStep(String correlationId, int step, String message, String transferType) {
        transferLogRepository.save(new com.banking.demo.domain.TransferLog(
                correlationId, step, message, transferType));
    }
}
