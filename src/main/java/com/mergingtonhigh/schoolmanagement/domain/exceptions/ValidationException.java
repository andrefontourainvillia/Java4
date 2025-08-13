package com.mergingtonhigh.schoolmanagement.domain.exceptions;

/**
 * Exception thrown when validation fails.
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}