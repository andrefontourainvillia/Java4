package com.mergingtonhigh.schoolmanagement.domain.exceptions;

/**
 * Exception thrown when authorization fails (authenticated but not authorized).
 */
public class AuthorizationException extends RuntimeException {
    
    public AuthorizationException(String message) {
        super(message);
    }
    
    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}