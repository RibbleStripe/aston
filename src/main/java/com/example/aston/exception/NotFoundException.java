package com.example.aston.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String string) {
        super(string);
    }
}
