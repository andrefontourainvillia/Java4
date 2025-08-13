package com.mergingtonhigh.schoolmanagement.application.mappers;

import org.springframework.stereotype.Component;

import com.mergingtonhigh.schoolmanagement.application.dtos.TeacherReferenceDTO;
import com.mergingtonhigh.schoolmanagement.domain.valueobjects.TeacherReference;

@Component
public class TeacherReferenceMapper {

    public TeacherReferenceDTO toDTO(TeacherReference teacherRef) {
        if (teacherRef == null) {
            return null;
        }

        return new TeacherReferenceDTO(
                teacherRef.getUsername(),
                teacherRef.getDisplayName(),
                teacherRef.getRole().name().toLowerCase());
    }
}
