package com.example.aston.exception;

public class NotEnoughFundsException extends RuntimeException {
    public NotEnoughFundsException() {
        super("Insufficient funds in the account.");
    }
}
