package com.example.smsserver.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final String userID;

    public UserNotFoundException(String userID) {
        super("User ID " + userID + " not found");
        this.userID = userID;
    }
}
