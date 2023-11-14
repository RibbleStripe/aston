package com.example.aston.repo;

import com.example.aston.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    List<Transaction> findTransactionsByAccountNumberFrom(String accountNumber);
}
