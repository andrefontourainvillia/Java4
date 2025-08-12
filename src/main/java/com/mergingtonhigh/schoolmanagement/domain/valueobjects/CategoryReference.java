package com.mergingtonhigh.schoolmanagement.domain.valueobjects;

import com.mergingtonhigh.schoolmanagement.domain.entities.ActivityCategory;

public class CategoryReference {

    private final String id;
    private final String label;
    private final String backgroundColor;
    private final String textColor;
    private final String description;

    public CategoryReference(String id, String label, String backgroundColor,
            String textColor, String description) {
        this.id = validateId(id);
        this.label = validateLabel(label);
        this.backgroundColor = validateColor(backgroundColor, "backgroundColor");
        this.textColor = validateColor(textColor, "textColor");
        this.description = description;
    }

    public static CategoryReference fromActivityCategory(ActivityCategory category) {
        return new CategoryReference(
                category.getId(),
                category.getLabel(),
                category.getBackgroundColor(),
                category.getTextColor(),
                category.getDescription());
    }

    private String validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da categoria não pode ser nulo ou vazio");
        }
        return id.trim().toLowerCase();
    }

    private String validateLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("Label da categoria não pode ser nulo ou vazio");
        }
        return label.trim();
    }

    private String validateColor(String color, String fieldName) {
        if (color == null || color.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " não pode ser nulo ou vazio");
        }

        String trimmedColor = color.trim();

        if (!trimmedColor.matches("^#[0-9a-fA-F]{6}$")) {
            throw new IllegalArgumentException(fieldName + " deve estar no formato hexadecimal (#RRGGBB)");
        }

        return trimmedColor;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        CategoryReference that = (CategoryReference) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("CategoryReference{id='%s', label='%s'}", id, label);
    }
}
