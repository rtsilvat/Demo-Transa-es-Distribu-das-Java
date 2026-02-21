package com.banking.demo.interfaces.rest;

import com.banking.demo.application.service.AccountService;
import com.banking.demo.domain.Account;
import com.banking.demo.domain.Transaction;
import com.banking.demo.interfaces.rest.dto.AccountResponse;
import com.banking.demo.interfaces.rest.dto.TransactionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> listAccounts() {
        List<Account> accounts = accountService.listAll();
        List<AccountResponse> response = accounts.stream()
                .map(a -> new AccountResponse(a.getId(), a.getNumber(), a.getHolder(), a.getBalance()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/statement")
    public ResponseEntity<List<TransactionResponse>> getStatement(
            @PathVariable Long id,
            @RequestParam(defaultValue = "50") int limit) {
        List<Transaction> transactions = accountService.getStatement(id, limit);
        List<TransactionResponse> response = transactions.stream()
                .map(t -> new TransactionResponse(
                        t.getId(), t.getAccountId(), t.getType(), t.getAmount(),
                        t.getBalanceAfter(), t.getTransferType(), t.getStatus(),
                        t.getCorrelationId(), t.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
