package com.example.aston.controller;

import com.example.aston.model.Transaction;
import com.example.aston.service.TransactionService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions() {
        List<Transaction> allTransactions = transactionService.getTransactions();
        return new ResponseEntity<>(allTransactions, HttpStatus.OK);
    }

    @GetMapping(value = "/{accountNumber}")
    public ResponseEntity<List<Transaction>> getTransactionsById(@PathVariable @NotBlank String accountNumber) {
        List<Transaction> allTransactionsByAccountNumber = transactionService.getTransactionsByAccountNumber(accountNumber);
        return new ResponseEntity<>(allTransactionsByAccountNumber, HttpStatus.OK);
    }
}
