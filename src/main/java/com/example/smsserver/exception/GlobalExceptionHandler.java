package com.example.smsserver.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e, HttpServletRequest request) {
        log.error("Request: {} \n Params: {} \n Log: {}", request.getRequestURI(), request.getParameterMap(),
                e.getMessage());
        return ResponseEntity.status(500).body("Something went wrong.");
    }
}
