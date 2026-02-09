package com.example.smsserver.exception;

import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {
    String ip;

    public RateLimitExceededException(String message, String ip) {
        super(message);
        this.ip = ip;
    }
}
