package com.mergingtonhigh.schoolmanagement.application.usecases;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mergingtonhigh.schoolmanagement.application.dtos.ActivityDTO;
import com.mergingtonhigh.schoolmanagement.application.services.ActivitySyncService;
import com.mergingtonhigh.schoolmanagement.domain.entities.Activity;
import com.mergingtonhigh.schoolmanagement.domain.repositories.ActivityRepository;
import com.mergingtonhigh.schoolmanagement.domain.valueobjects.DifficultyLevel;
import com.mergingtonhigh.schoolmanagement.application.mappers.ActivityMapper;

@Service
public class ActivityUseCase {

    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;
    private final ActivitySyncService activitySyncService;

    public ActivityUseCase(ActivityRepository activityRepository, ActivityMapper activityMapper,
            ActivitySyncService activitySyncService) {
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
        this.activitySyncService = activitySyncService;
    }

    public Map<String, ActivityDTO> getActivities(String day, String startTime, String endTime, String category) {
        return getActivities(day, startTime, endTime, category, null);
    }

    public Map<String, ActivityDTO> getActivities(String day, String startTime, String endTime, String category, String difficulty) {
        List<Activity> activities;

        if (day != null && startTime != null && endTime != null) {
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);
            activities = activityRepository.findByDayAndTimeRange(day, start, end);
        } else if (day != null) {
            activities = activityRepository.findByDay(day);
        } else if (startTime != null && endTime != null) {
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);
            activities = activityRepository.findByTimeRange(start, end);
        } else {
            activities = activityRepository.findAll();
        }

        // Garante que dados embarcados estão sincronizados
        activities.forEach(activity -> {
            if (needsSync(activity)) {
                activitySyncService.syncActivityEmbeddedData(activity.getName());
            }
        });

        Map<String, ActivityDTO> activityMap = activities.stream()
                .map(activityMapper::toDTO)
                .collect(Collectors.toMap(ActivityDTO::name, dto -> dto));

        // Filtrar por categoria se especificada
        if (category != null && !category.trim().isEmpty()) {
            activityMap = activityMap.entrySet().stream()
                    .filter(entry -> category.equals(entry.getValue().category()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        // Filtrar por dificuldade se especificada
        if (difficulty != null && !difficulty.trim().isEmpty()) {
            if ("todos".equalsIgnoreCase(difficulty.trim())) {
                // Mostrar apenas atividades sem dificuldade especificada (para todos os níveis)
                activityMap = activityMap.entrySet().stream()
                        .filter(entry -> entry.getValue().difficultyLevel() == null)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            } else {
                // Mostrar atividades do nível específico
                DifficultyLevel difficultyLevel = DifficultyLevel.fromString(difficulty);
                if (difficultyLevel != null) {
                    String targetDifficulty = difficultyLevel.getDisplayName();
                    activityMap = activityMap.entrySet().stream()
                            .filter(entry -> targetDifficulty.equals(entry.getValue().difficultyLevel()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                }
            }
        }

        return activityMap;
    }

    private boolean needsSync(Activity activity) {
        // Verifica se dados embarcados estão vazios mas existem referências
        boolean needsCategorySync = activity.getCategoryId() != null && activity.getCategory() == null;
        boolean needsTeacherSync = !activity.getAssignedTeachers().isEmpty() &&
                activity.getAssignedTeacherReferences().isEmpty();
        return needsCategorySync || needsTeacherSync;
    }

    public Map<String, ActivityDTO> getActivities(String day, String startTime, String endTime) {
        return getActivities(day, startTime, endTime, null);
    }

    public List<String> getAvailableDays() {
        return activityRepository.findAllUniqueDays()
                .stream()
                .sorted()
                .collect(Collectors.toList());
    }
}