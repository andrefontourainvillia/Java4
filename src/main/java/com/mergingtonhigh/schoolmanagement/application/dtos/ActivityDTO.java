package com.mergingtonhigh.schoolmanagement.application.dtos;

import java.util.List;

public record ActivityDTO(
                String name,
                String description,
                String schedule,
                ScheduleDetailsDTO scheduleDetails,
                int maxParticipants,
                List<String> participants,
                int currentParticipantCount,
                String category,
                ActivityCategoryDTO categoryDetails,
                List<String> assignedTeachers, // Para compatibilidade com APIs existentes
                List<TeacherReferenceDTO> teacherDetails) { // Dados embarcados otimizados
        public record ScheduleDetailsDTO(
                        List<String> days,
                        String startTime,
                        String endTime) {
        }
}