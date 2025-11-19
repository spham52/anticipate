package com.example.smsserver.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("Request: {} \n Params: {} \n Log: {}", request.getRequestURI(), request.getParameterMap(),
                e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e,
                                                         HttpServletRequest request) {
        log.error("Runtime exception - Request: {} \n Params: {} \n Log: {}",
                request.getRequestURI(), request.getParameterMap(), e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }

    @ExceptionHandler(SensorDoesNotExistException.class)
    public ResponseEntity<String> handleSensorDoesNotExistException(SensorDoesNotExistException e, HttpServletRequest request) {
        log.warn("Sensor {} does not exist.\nRequest: {}\n Params: {}",
                e.getSensorID(), request.getRequestURI(), request.getParameterMap());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(SensorAlreadyAssociatedWithUserException.class)
    public ResponseEntity<String> handleSensorAlreadyAssociatedWithUserException
            (SensorAlreadyAssociatedWithUserException e,
             HttpServletRequest request) {
        log.warn("Sensor {} is already associated with a user.\nRequest: {}\n Params: {}",
                e.getSensorID(), request.getRequestURI(), request.getParameterMap());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException (UserNotFoundException e, HttpServletRequest request) {
        log.warn("User with ID: {} not found.\nRequest: {}\n Params: {}\n",
                e.getUserID(), request.getRequestURI(), request.getParameterMap());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException (UserAlreadyExistsException e,
                                                                    HttpServletRequest request) {
        log.warn("User with ID: {} already exists in the DB.\nRequest: {}\n Params: {}\n",
                e.getUserID(), request.getRequestURI(), request.getParameterMap());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
