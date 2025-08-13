package com.mergingtonhigh.schoolmanagement.application.usecases;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mergingtonhigh.schoolmanagement.application.dtos.ActivityDTO;
import com.mergingtonhigh.schoolmanagement.domain.entities.Activity;
import com.mergingtonhigh.schoolmanagement.domain.repositories.ActivityRepository;
import com.mergingtonhigh.schoolmanagement.application.mappers.ActivityMapper;

@Service
public class ActivityUseCase {

    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;

    public ActivityUseCase(ActivityRepository activityRepository, ActivityMapper activityMapper) {
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
    }

    public Map<String, ActivityDTO> getActivities(String day, String startTime, String endTime, String category) {
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

        Map<String, ActivityDTO> activityMap = activities.stream()
                .map(activityMapper::toDTO)
                .collect(Collectors.toMap(ActivityDTO::name, dto -> dto));

        if (category != null && !category.trim().isEmpty()) {
            return activityMap.entrySet().stream()
                    .filter(entry -> category.equals(entry.getValue().category().name()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        return activityMap;
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