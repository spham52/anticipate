package com.example.smsserver.exception;

import lombok.Getter;

// general exception class when userID is already found in DB
@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private final String userID;

    public UserAlreadyExistsException(String userID) {
        super("User ID: " + userID + " already exists in the DB!");
        this.userID = userID;
    }
}
