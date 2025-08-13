package com.mergingtonhigh.schoolmanagement.application.dtos;

import java.util.List;

import com.mergingtonhigh.schoolmanagement.domain.enums.ActivityCategory;

public record ActivityDTO(
                String name,
                String description,
                String schedule,
                ScheduleDetailsDTO scheduleDetails,
                int maxParticipants,
                List<String> participants,
                int currentParticipantCount,
                ActivityCategory category,
                boolean canTeachersRegisterStudents) {
        public record ScheduleDetailsDTO(
                        List<String> days,
                        String startTime,
                        String endTime) {
        }
}