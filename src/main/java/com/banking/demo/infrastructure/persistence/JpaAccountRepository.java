package com.banking.demo.infrastructure.persistence;

import com.banking.demo.application.port.AccountRepository;
import com.banking.demo.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaAccountRepository extends JpaRepository<Account, Long>, AccountRepository {

    @Override
    default List<Account> findAll() {
        return findAllByOrderByIdAsc();
    }

    List<Account> findAllByOrderByIdAsc();
}
