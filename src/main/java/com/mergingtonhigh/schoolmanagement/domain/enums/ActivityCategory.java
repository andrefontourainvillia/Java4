package com.mergingtonhigh.schoolmanagement.domain.enums;

public enum ActivityCategory {
    SPORTS("Esportes"),
    ACADEMIC("Acadêmica"),
    ARTS("Artes"),
    CLUBS("Clubes"),
    TECHNOLOGY("Tecnologia");

    private final String displayName;

    ActivityCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}