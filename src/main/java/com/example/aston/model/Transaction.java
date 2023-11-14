package com.example.aston.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "transactions")
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Account number from is required.")
    private String accountNumberFrom;
    @NotBlank(message = "Account number to is required.")
    private String accountNumberTo;
    private LocalTime time;
    @Column(columnDefinition = "numeric")
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private Operation operation;

    public Transaction(String accountNumberFrom, String accountNumberTo, LocalTime time,
                       BigDecimal amount, Operation operation) {
        this.accountNumberFrom = accountNumberFrom;
        this.accountNumberTo = accountNumberTo;
        this.time = time;
        this.amount = amount;
        this.operation = operation;
    }
}
