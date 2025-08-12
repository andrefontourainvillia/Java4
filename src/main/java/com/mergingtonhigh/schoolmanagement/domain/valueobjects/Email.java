package com.mergingtonhigh.schoolmanagement.domain.valueobjects;

public record Email(String value) {
    public Email {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser nulo ou vazio");
        }
        if (!isValidEmail(value.trim())) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
        value = value.trim().toLowerCase();
    }

    private static boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    @Override
    public String toString() {
        return value;
    }
}