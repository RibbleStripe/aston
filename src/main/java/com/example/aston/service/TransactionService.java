package com.example.aston.service;

import java.util.List;

import com.example.aston.model.Transaction;

public interface TransactionService {
    List<Transaction> getTransactions();
    List<Transaction> getTransactionsByAccountNumber(String accountNumber);
}
