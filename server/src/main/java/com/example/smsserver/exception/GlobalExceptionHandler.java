package com.example.smsserver.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SensorDoesNotExistException.class)
    public ResponseEntity<ErrorResponse> handleSensorDoesNotExist(
            SensorDoesNotExistException e,
            HttpServletRequest request) {
        log.warn("Sensor {} does not exist", e.getSensorID());
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(SensorAlreadyAssociatedWithUserException.class)
    public ResponseEntity<ErrorResponse> handleSensorAlreadyAssociated(
            SensorAlreadyAssociatedWithUserException e,
            HttpServletRequest request) {
        log.warn("Sensor {} is already associated with a user", e.getSensorID());
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException e,
            HttpServletRequest request) {
        log.warn("User {} not found", e.getUserID());
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException e,
            HttpServletRequest request) {
        log.warn("User already exists");
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UnauthorisedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorisedAccess(
            UnauthorisedAccessException e,
            HttpServletRequest request) {
        log.warn("Unauthorised access attempt");
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException e,
            HttpServletRequest request) {
        log.warn("Invalid argument: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception e,
            HttpServletRequest request) {
        log.error("Unexpected error occurred", e);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        log.warn("Validation failed: {}", message);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    @ExceptionHandler(GoogleRecaptchaFailedException.class)
    public ResponseEntity<ErrorResponse> handleGoogleRecaptchaFailed(
            GoogleRecaptchaFailedException e, HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                "Please complete the captcha and try again.",
                request.getRequestURI());
    }

    // helper method to build error responses
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            String path) {

        ErrorResponse error = ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(error);
    }
}