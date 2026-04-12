package com.goodskill.ai.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST controllers
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        log.debug("Response status exception: {}", ex.getReason(), ex);
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getReason());
        body.put("status", ex.getStatusCode().value());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        log.debug("Validation exception: {}", ex.getMessage(), ex);
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Validation failed");
        body.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleBindException(BindException ex) {
        log.debug("Bind exception: {}", ex.getMessage(), ex);
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Binding failed");
        body.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.debug("Constraint violation exception: {}", ex.getMessage(), ex);
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.debug("Illegal argument exception: {}", ex.getMessage(), ex);
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Internal server error");
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
