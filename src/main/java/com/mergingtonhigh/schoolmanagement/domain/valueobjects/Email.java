package com.mergingtonhigh.schoolmanagement.domain.valueobjects;

import java.util.regex.Pattern;

public record Email(String value) {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    public Email {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser nulo ou vazio");
        }
        
        String trimmedEmail = value.trim().toLowerCase();
        
        if (!isValidEmail(trimmedEmail)) {
            throw new IllegalArgumentException("Formato de email inválido: " + value);
        }
        
        if (trimmedEmail.length() > 254) { // RFC 5321 limit
            throw new IllegalArgumentException("Email muito longo (máximo 254 caracteres)");
        }
        
        value = trimmedEmail;
    }

    private static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public String toString() {
        return value;
    }
}