package com.mergingtonhigh.schoolmanagement.application.dtos;

public record ActivityCategoryDTO(
        String id,
        String type,
        String label,
        String backgroundColor,
        String textColor,
        String description,
        boolean active) {
}
