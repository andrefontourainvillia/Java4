package com.mergingtonhigh.schoolmanagement.presentation.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mergingtonhigh.schoolmanagement.application.dtos.ActivityCategoryDTO;
import com.mergingtonhigh.schoolmanagement.application.usecases.ActivityCategoryUseCase;

@RestController
@RequestMapping("/categories")
public class ActivityCategoryController {

    private final ActivityCategoryUseCase categoryUseCase;

    public ActivityCategoryController(ActivityCategoryUseCase categoryUseCase) {
        this.categoryUseCase = categoryUseCase;
    }

    @GetMapping
    public ResponseEntity<Map<String, ActivityCategoryDTO>> getActiveCategories() {
        Map<String, ActivityCategoryDTO> categories = categoryUseCase.getActiveCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ActivityCategoryDTO>> getAllCategories() {
        List<ActivityCategoryDTO> categories = categoryUseCase.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{typeCode}")
    public ResponseEntity<ActivityCategoryDTO> getCategoryByType(@PathVariable String typeCode) {
        return categoryUseCase.getCategoryByType(typeCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{typeCode}")
    public ResponseEntity<ActivityCategoryDTO> saveCategory(
            @PathVariable String typeCode,
            @RequestParam String label,
            @RequestParam String backgroundColor,
            @RequestParam String textColor,
            @RequestParam(required = false, defaultValue = "") String description) {

        try {
            ActivityCategoryDTO category = categoryUseCase.saveCategory(
                    typeCode, label, backgroundColor, textColor, description);
            return ResponseEntity.ok(category);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{typeCode}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateCategory(@PathVariable String typeCode) {
        try {
            categoryUseCase.deactivateCategory(typeCode);
            return ResponseEntity.ok(Map.of("message", "Categoria desativada com sucesso"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
