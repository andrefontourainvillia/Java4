package com.mergingtonhigh.schoolmanagement.application.mappers;

import org.springframework.stereotype.Component;

import com.mergingtonhigh.schoolmanagement.application.dtos.ActivityDTO;
import com.mergingtonhigh.schoolmanagement.domain.entities.Activity;

@Component
public class ActivityMapper {

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

        return new ActivityDTO(
                activity.getName(),
                activity.getDescription(),
                null,
                scheduleDetailsDTO,
                activity.getMaxParticipants(),
                activity.getParticipants(),
                activity.getCurrentParticipantCount(),
                activity.getCategory(),
                activity.canTeachersRegisterStudents());
    }
}