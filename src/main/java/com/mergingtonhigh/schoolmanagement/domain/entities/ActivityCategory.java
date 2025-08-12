package com.mergingtonhigh.schoolmanagement.domain.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "activity_categories")
public class ActivityCategory {

    @Id
    private String id;
    private String label;
    private String backgroundColor;
    private String textColor;
    private String description;
    private boolean active;

    public ActivityCategory() {
        this.active = true;
    }

    public ActivityCategory(String id, String label, String backgroundColor,
            String textColor, String description) {
        this.id = validateId(id);
        this.label = validateLabel(label);
        this.backgroundColor = validateColor(backgroundColor, "backgroundColor");
        this.textColor = validateColor(textColor, "textColor");
        this.description = description;
        this.active = true;
    }

    private String validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID não pode ser nulo ou vazio");
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

    public void setId(String id) {
        this.id = validateId(id);
    }

    public String getType() {
        return id;
    }

    public void setType(String categoryType) {
        this.id = validateId(categoryType);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = validateLabel(label);
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = validateColor(backgroundColor, "backgroundColor");
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = validateColor(textColor, "textColor");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
