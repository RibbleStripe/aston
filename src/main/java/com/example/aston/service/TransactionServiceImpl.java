package com.example.aston.service;

import com.example.aston.exception.NotFoundException;
import com.example.aston.model.Transaction;
import com.example.aston.repo.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> getTransactionsByAccountNumber(String accountNumber) {
        List<Transaction> transactions = transactionRepository.findTransactionsByAccountNumberFrom(accountNumber);
        if (transactions.isEmpty()) {
            throw new NotFoundException("No transactions found for account number: " + accountNumber);
        }
        return transactions;
    }
}
