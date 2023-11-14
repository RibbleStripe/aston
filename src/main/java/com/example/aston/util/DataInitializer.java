package com.example.aston.util;

import com.example.aston.model.Account;
import com.example.aston.model.Operation;
import com.example.aston.model.Transaction;
import com.example.aston.repo.AccountRepository;
import com.example.aston.repo.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    @Override
    public void run(String... args) {
        Account account1 = new Account("Name Surname1", "1111");
        account1.setAmount(BigDecimal.valueOf(1000));
        accountRepository.save(account1);

        Account account2 = new Account("Name Surname2", "2222");
        account2.setAmount(BigDecimal.valueOf(2000));
        accountRepository.save(account2);

        Transaction transaction1 = new Transaction(account1.getAccountNumber(),
                account2.getAccountNumber(),
                LocalTime.now(), BigDecimal.valueOf(500), Operation.DEPOSIT);
        transactionRepository.save(transaction1);

        Transaction transaction2 = new Transaction(account2.getAccountNumber(),
                account1.getAccountNumber(),
                LocalTime.now(), BigDecimal.valueOf(500), Operation.WITHDRAW);
        transactionRepository.save(transaction2);
    }
}
