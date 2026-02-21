package com.banking.demo.application.port;

import com.banking.demo.domain.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Optional<Account> findById(Long id);

    List<Account> findAll();

    Account save(Account account);
}
