package com.example.ims.Exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleArgumentExceptions(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> handleArgumentExceptions(Exception ex){
        Map<String,String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, String>> handleTransactionSystemException(TransactionSystemException ex) {
        Throwable cause = ex.getRootCause();
        if (cause instanceof ConstraintViolationException) {
            ConstraintViolationException validationException = (ConstraintViolationException) cause;
            Map<String, String> errors = new HashMap<>();
            validationException.getConstraintViolations().forEach(violation -> 
                errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("error", "An unexpected error occurred");
            return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationExceptions(DataIntegrityViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        
        // You can log the exception message or extract specific information
        errors.put("error", ex.getMostSpecificCause().getMessage());

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
