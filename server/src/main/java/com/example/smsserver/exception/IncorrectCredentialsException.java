package com.example.smsserver.exception;

public class IncorrectCredentialsException extends RuntimeException {
  private final String message;

  public IncorrectCredentialsException() {
    message = "Incorrect credentials";
  }
}
