package com.example.aston.exception;

public class WrongPinException extends RuntimeException {
    public WrongPinException() {
        super("Wrong PIN code.");
    }
}
