package com.banking.demo.interfaces.rest.dto;

import java.math.BigDecimal;

public class AccountResponse {

    private final Long id;
    private final String number;
    private final String holder;
    private final BigDecimal balance;

    public AccountResponse(Long id, String number, String holder, BigDecimal balance) {
        this.id = id;
        this.number = number;
        this.holder = holder;
        this.balance = balance;
    }

    public Long getId() { return id; }
    public String getNumber() { return number; }
    public String getHolder() { return holder; }
    public BigDecimal getBalance() { return balance; }
}
