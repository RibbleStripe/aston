package com.example.aston.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentDTO {
    private String pinCode;
    private BigDecimal amount;
}
