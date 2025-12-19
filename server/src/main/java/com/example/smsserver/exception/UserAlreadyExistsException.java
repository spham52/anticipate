package com.example.smsserver.exception;

import lombok.Getter;

// general exception class when userID is already found in DB
@Getter
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
    }
}
