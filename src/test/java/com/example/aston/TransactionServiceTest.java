package com.example.aston;

import com.example.aston.exception.NotFoundException;
import com.example.aston.model.Operation;
import com.example.aston.model.Transaction;
import com.example.aston.repo.TransactionRepository;
import com.example.aston.service.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    public void testGetAllTransactions() {
        Transaction transaction1 = new Transaction(1L, "11111", "11111",
                LocalTime.now(), BigDecimal.TEN, Operation.DEPOSIT);

        Transaction transaction2 = new Transaction(2L, "22222", "22222",
                LocalTime.now(), BigDecimal.TEN, Operation.TRANSFER);

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactions();

        assertEquals(transactions, result);
    }

    @Test
    public void testGetAllTransactionsByAccountNumber_WhenNoTransactionsFound() {
        List<Transaction> emptyList = Collections.emptyList();
        when(transactionRepository.findTransactionsByAccountNumberFrom("11111")).thenReturn(emptyList);
        assertThrows(NotFoundException.class, () -> transactionService.getTransactionsByAccountNumber("11111"));
    }

    @Test
    public void testGetAllTransactionsByAccountNumber_WhenMultipleTransactionsFound() {
        Transaction transaction1 = new Transaction(1L, "11111", "11111",
                LocalTime.now(), BigDecimal.TEN, Operation.DEPOSIT);

        Transaction transaction2 = new Transaction(2L, "11111", "22222",
                LocalTime.now(), BigDecimal.TEN, Operation.TRANSFER);

        when(transactionRepository.findTransactionsByAccountNumberFrom("11111")).thenReturn(Arrays.asList(transaction1, transaction2));

        List<Transaction> result = transactionService.getTransactionsByAccountNumber("11111");

        assertEquals(Arrays.asList(transaction1, transaction2), result);
    }

    @Test
    public void testGetAllTransactionsByAccountNumber() {
        Transaction transaction1 = new Transaction(1L, "11111", "11111",
                LocalTime.now(), BigDecimal.TEN, Operation.DEPOSIT);

        new Transaction(2L, "22222", "22222",
                LocalTime.now(), BigDecimal.TEN, Operation.TRANSFER);

        when(transactionRepository.findTransactionsByAccountNumberFrom("11111")).thenReturn(List.of(transaction1));

        List<Transaction> result = transactionService.getTransactionsByAccountNumber("11111");

        assertEquals(List.of(transaction1), result);
    }
}
