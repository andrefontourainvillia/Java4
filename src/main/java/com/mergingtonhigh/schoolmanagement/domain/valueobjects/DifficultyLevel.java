package com.mergingtonhigh.schoolmanagement.domain.valueobjects;

/**
 * Enumeração que representa os níveis de dificuldade das atividades.
 */
public enum DifficultyLevel {
    INICIANTE("Iniciante"),
    INTERMEDIARIO("Intermediário"),
    AVANCADO("Avançado");

    private final String displayName;

    DifficultyLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Converte uma string para DifficultyLevel, ignorando case.
     * @param value A string a ser convertida
     * @return O DifficultyLevel correspondente, ou null se não encontrado
     */
    public static DifficultyLevel fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        String normalizedValue = value.trim();
        for (DifficultyLevel level : values()) {
            if (level.name().equalsIgnoreCase(normalizedValue) || 
                level.displayName.equalsIgnoreCase(normalizedValue)) {
                return level;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}