package com.mergingtonhigh.schoolmanagement.presentation.mappers;

import org.springframework.stereotype.Component;

import com.mergingtonhigh.schoolmanagement.application.dtos.ActivityCategoryDTO;
import com.mergingtonhigh.schoolmanagement.domain.entities.ActivityCategory;

@Component
public class ActivityCategoryMapper {

    public ActivityCategoryDTO toDTO(ActivityCategory category) {
        if (category == null) {
            return null;
        }

        return new ActivityCategoryDTO(
                category.getId(),
                category.getType(),
                category.getLabel(),
                category.getBackgroundColor(),
                category.getTextColor(),
                category.getDescription(),
                category.isActive());
    }
}
