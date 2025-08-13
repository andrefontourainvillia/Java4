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

import com.mergingtonhigh.schoolmanagement.application.dtos.ActivityDTO;
import com.mergingtonhigh.schoolmanagement.application.usecases.ActivityUseCase;
import com.mergingtonhigh.schoolmanagement.application.usecases.StudentRegistrationUseCase;
import com.mergingtonhigh.schoolmanagement.domain.exceptions.AuthenticationException;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityUseCase activityUseCase;
    private final StudentRegistrationUseCase studentRegistrationUseCase;

    public ActivityController(ActivityUseCase activityUseCase,
            StudentRegistrationUseCase studentRegistrationUseCase) {
        this.activityUseCase = activityUseCase;
        this.studentRegistrationUseCase = studentRegistrationUseCase;
    }

    @GetMapping
    public ResponseEntity<Map<String, ActivityDTO>> getActivities(
            @RequestParam(required = false) String day,
            @RequestParam(name = "start_time", required = false) String startTime,
            @RequestParam(name = "end_time", required = false) String endTime,
            @RequestParam(required = false) String category) {

        Map<String, ActivityDTO> activities = activityUseCase.getActivities(day, startTime, endTime, category);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/days")
    public ResponseEntity<List<String>> getAvailableDays() {
        List<String> days = activityUseCase.getAvailableDays();
        return ResponseEntity.ok(days);
    }

    @PostMapping("/{activityName}/signup")
    public ResponseEntity<Map<String, String>> signupForActivity(
            @PathVariable String activityName,
            @RequestParam String email,
            @RequestParam(name = "teacher_username", required = false) String teacherUsername) {

        if (teacherUsername == null || teacherUsername.trim().isEmpty()) {
            throw new AuthenticationException("Autenticação necessária para esta ação");
        }

        String message = studentRegistrationUseCase.signupForActivity(activityName, email, teacherUsername);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/{activityName}/unregister")
    public ResponseEntity<Map<String, String>> unregisterFromActivity(
            @PathVariable String activityName,
            @RequestParam String email,
            @RequestParam(name = "teacher_username", required = false) String teacherUsername) {

        if (teacherUsername == null || teacherUsername.trim().isEmpty()) {
            throw new AuthenticationException("Autenticação necessária para esta ação");
        }

        String message = studentRegistrationUseCase.unregisterFromActivity(activityName, email, teacherUsername);
        return ResponseEntity.ok(Map.of("message", message));
    }
}