package com.example.aston.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "accounts")
@AllArgsConstructor
public class Account {

    @Id
    private String accountNumber;
    @NotBlank(message = "Name is required.")
    private String name;
    @Column(length = 4)
    @NotBlank(message = "PIN is required.")
    private String pinCode;
    @Column(columnDefinition = "numeric")
    private BigDecimal amount;

    public Account(String name, String pinCode) {
        this.accountNumber = UUID.randomUUID().toString();
        this.name = name;
        this.pinCode = pinCode;
        this.amount = BigDecimal.valueOf(0);
    }

}
