package com.mergingtonhigh.schoolmanagement.application.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.mergingtonhigh.schoolmanagement.application.dtos.ActivityCategoryDTO;
import com.mergingtonhigh.schoolmanagement.application.dtos.ActivityDTO;
import com.mergingtonhigh.schoolmanagement.application.dtos.TeacherReferenceDTO;
import com.mergingtonhigh.schoolmanagement.domain.entities.Activity;

@Component
public class ActivityMapper {

    private final TeacherReferenceMapper teacherReferenceMapper;

    public ActivityMapper(TeacherReferenceMapper teacherReferenceMapper) {
        this.teacherReferenceMapper = teacherReferenceMapper;
    }

    public ActivityDTO toDTO(Activity activity) {
        if (activity == null) {
            return null;
        }

        ActivityDTO.ScheduleDetailsDTO scheduleDetailsDTO = null;
        if (activity.getScheduleDetails() != null) {
            scheduleDetailsDTO = new ActivityDTO.ScheduleDetailsDTO(
                    activity.getScheduleDetails().days(),
                    activity.getScheduleDetails().startTime().toString(),
                    activity.getScheduleDetails().endTime().toString());
        }

        // Usa dados embarcados da categoria se disponível
        String categoryCode = null;
        ActivityCategoryDTO categoryDetails = null;

        if (activity.getCategory() != null) {
            categoryCode = activity.getCategory().getId();
            categoryDetails = new ActivityCategoryDTO(
                    activity.getCategory().getId(),
                    activity.getCategory().getId(), // type = id
                    activity.getCategory().getLabel(),
                    activity.getCategory().getBackgroundColor(),
                    activity.getCategory().getTextColor(),
                    activity.getCategory().getDescription(),
                    true // Assume ativo se embarcado
            );
        } else if (activity.getCategoryId() != null) {
            categoryCode = activity.getCategoryId();
            // categoryDetails permanece null, será carregado por consulta se necessário
        }

        // Usa dados embarcados dos professores
        List<String> assignedTeachers = activity.getAssignedTeachers();
        List<TeacherReferenceDTO> teacherDetails = activity.getAssignedTeacherReferences()
                .stream()
                .map(teacherReferenceMapper::toDTO)
                .collect(Collectors.toList());

        return new ActivityDTO(
                activity.getName(),
                activity.getDescription(),
                null,
                scheduleDetailsDTO,
                activity.getMaxParticipants(),
                activity.getParticipants(),
                activity.getCurrentParticipantCount(),
                categoryCode,
                categoryDetails,
                assignedTeachers,
                teacherDetails);
    }
}