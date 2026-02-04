package com.example.smsserver.exception;

public class GoogleRecaptchaFailedException extends RuntimeException {
    private String message;

    public GoogleRecaptchaFailedException() {
        super("Captcha verification failed.");
    }
}
