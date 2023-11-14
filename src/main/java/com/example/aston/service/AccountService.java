package com.example.aston.service;

import com.example.aston.model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    Optional<Account> createAccount(String name, String pin);

    List<Account> getAccounts();

    Optional<Account> getAccountByAccountNumber(String accountNumber);

    void deposit(String accountNumber, BigDecimal amount);

    void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount, String pinCode);

    void withdraw(String accountNumber, BigDecimal amount, String pinCode);
}
