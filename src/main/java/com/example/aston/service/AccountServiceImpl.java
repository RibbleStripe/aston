package com.example.aston.service;

import com.example.aston.exception.WrongPinException;
import com.example.aston.exception.NotEnoughFundsException;
import com.example.aston.exception.NotFoundException;
import com.example.aston.model.Account;
import com.example.aston.model.Operation;
import com.example.aston.model.Transaction;
import com.example.aston.repo.AccountRepository;
import com.example.aston.repo.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Optional<Account> createAccount(String name, String pinCode) {
        if ((name == null) || (pinCode.length() != 4)) {
            throw new WrongPinException();
        }
        Account account = new Account(name, pinCode);
        accountRepository.save(account);
        return Optional.of(account);
    }

    @Override
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findById(accountNumber);
    }

    @Override
    @Transactional
    public void deposit(String accountNumber, BigDecimal amount) {
        Optional<Account> id = accountRepository.findById(accountNumber);

        if (id.isEmpty()) {
            throw new NotFoundException("Account with this number not found.");
        }

        Account account = id.get();
        Transaction transaction = new Transaction(accountNumber, accountNumber,
                LocalTime.now(), amount, Operation.DEPOSIT);
        account.setAmount(account.getAmount().add(amount));
        accountRepository.save(account);
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount, String pinCode) {
        Optional<Account> fromAccount = accountRepository.findById(fromAccountNumber);
        Optional<Account> toAccount = accountRepository.findById(toAccountNumber);

        if ((fromAccount.isEmpty()) || (toAccount.isEmpty())) {
            throw new NotFoundException("Transfer error.");
        }
        if (!(fromAccount.get().getPinCode().equals(pinCode))) {
            throw new WrongPinException();
        }
        if (fromAccount.get().getAmount().compareTo(amount) < 0) {
            throw new NotEnoughFundsException();
        }

        fromAccount.get().setAmount(fromAccount.get().getAmount().subtract(amount));
        toAccount.get().setAmount(toAccount.get().getAmount().add(amount));

        Transaction transaction = new Transaction(fromAccountNumber, toAccountNumber, LocalTime.now(),
                amount, Operation.TRANSFER);

        accountRepository.save(fromAccount.get());
        accountRepository.save(toAccount.get());
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void withdraw(String accountNumber, BigDecimal amount, String pinCode) {
        Optional<Account> id = accountRepository.findById(accountNumber);

        if (id.isEmpty()) {
            throw new NotFoundException("Account with this number not found.");
        }
        if (id.get().getAmount().compareTo(amount) < 0) {
            throw new NotEnoughFundsException();
        }
        if (!(id.get().getPinCode().equals(pinCode))) {
            throw new WrongPinException();

        }
        Account account = id.get();
        Transaction transaction = new Transaction(accountNumber, accountNumber, LocalTime.now(),
                amount, Operation.WITHDRAW);

        account.setAmount(account.getAmount().subtract(amount));

        accountRepository.save(account);
        transactionRepository.save(transaction);
    }
}
