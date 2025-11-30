package com.uxcorp.url_shortener.helper;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.BindException;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class ResponseHelper {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                String firstErrorMessage = ex.getBindingResult().getFieldErrors().stream()
                                .findFirst()
                                .map(error -> error.getDefaultMessage())
                                .orElse("Validation failed");

                return ResponseEntity.status(422).body(Map.of(
                                "message", firstErrorMessage,
                                "errors", errors));
        }

        @ExceptionHandler(BindException.class)
        public ResponseEntity<?> handleBindException(BindException ex) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                String firstErrorMessage = ex.getBindingResult().getFieldErrors().stream()
                                .findFirst()
                                .map(error -> error.getDefaultMessage())
                                .orElse("Validation failed");

                return ResponseEntity.status(422).body(Map.of(
                                "message", firstErrorMessage,
                                "errors", errors));
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
                Map<String, String> errors = new HashMap<>();
                ex.getConstraintViolations()
                                .forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));

                String firstErrorMessage = ex.getConstraintViolations().stream()
                                .findFirst()
                                .map(v -> v.getMessage())
                                .orElse("Validation failed");

                return ResponseEntity.status(422).body(Map.of(
                                "message", firstErrorMessage,
                                "errors", errors));
        }
}