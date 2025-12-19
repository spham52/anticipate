package com.example.smsserver.exception;

public class UnauthorisedAccessException extends RuntimeException {
    public UnauthorisedAccessException(String message) {
        super(message);
    }
}
