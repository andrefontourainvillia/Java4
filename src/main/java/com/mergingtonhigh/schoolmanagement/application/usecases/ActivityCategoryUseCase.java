package com.mergingtonhigh.schoolmanagement.application.usecases;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mergingtonhigh.schoolmanagement.application.dtos.ActivityCategoryDTO;
import com.mergingtonhigh.schoolmanagement.application.services.ActivitySyncService;
import com.mergingtonhigh.schoolmanagement.domain.entities.ActivityCategory;
import com.mergingtonhigh.schoolmanagement.domain.repositories.ActivityCategoryRepository;
import com.mergingtonhigh.schoolmanagement.application.mappers.ActivityCategoryMapper;

@Service
public class ActivityCategoryUseCase {

    private final ActivityCategoryRepository categoryRepository;
    private final ActivityCategoryMapper categoryMapper;
    private final ActivitySyncService activitySyncService;

    public ActivityCategoryUseCase(ActivityCategoryRepository categoryRepository,
            ActivityCategoryMapper categoryMapper,
            ActivitySyncService activitySyncService) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.activitySyncService = activitySyncService;
    }

    public Map<String, ActivityCategoryDTO> getActiveCategories() {
        return categoryRepository.findAllActive()
                .stream()
                .collect(Collectors.toMap(
                        ActivityCategory::getType,
                        categoryMapper::toDTO));
    }

    public List<ActivityCategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ActivityCategoryDTO> getCategoryByType(String typeCode) {
        if (typeCode == null || typeCode.trim().isEmpty()) {
            return Optional.empty();
        }
        String normalizedType = typeCode.trim().toLowerCase();
        return categoryRepository.findById(normalizedType)
                .map(categoryMapper::toDTO);
    }

    public ActivityCategoryDTO saveCategory(String typeCode, String label,
            String backgroundColor, String textColor,
            String description) {
        if (typeCode == null || typeCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Código da categoria não pode ser nulo ou vazio");
        }

        String normalizedType = typeCode.trim().toLowerCase();

        Optional<ActivityCategory> existingCategory = categoryRepository.findById(normalizedType);

        ActivityCategory category;
        if (existingCategory.isPresent()) {
            category = existingCategory.get();
            category.setLabel(label);
            category.setBackgroundColor(backgroundColor);
            category.setTextColor(textColor);
            category.setDescription(description);
            category.activate();
        } else {
            category = new ActivityCategory(normalizedType, label, backgroundColor, textColor, description);
        }

        ActivityCategory savedCategory = categoryRepository.save(category);

        // Sincroniza dados embarcados em todas as atividades relacionadas
        activitySyncService.syncCategoryDataInActivities(savedCategory);

        return categoryMapper.toDTO(savedCategory);
    }

    public void deactivateCategory(String typeCode) {
        if (typeCode == null || typeCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de categoria inválido: " + typeCode);
        }

        String normalizedType = typeCode.trim().toLowerCase();
        Optional<ActivityCategory> category = categoryRepository.findById(normalizedType);

        if (category.isPresent()) {
            ActivityCategory cat = category.get();
            cat.deactivate();
            categoryRepository.save(cat);

            // Sincroniza dados embarcados em todas as atividades relacionadas
            activitySyncService.syncCategoryDataInActivities(cat);
        } else {
            throw new IllegalArgumentException("Categoria não encontrada: " + typeCode);
        }
    }

}
