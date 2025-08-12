package com.mergingtonhigh.schoolmanagement.application.usecases;

import org.springframework.stereotype.Service;

import com.mergingtonhigh.schoolmanagement.domain.entities.Activity;
import com.mergingtonhigh.schoolmanagement.domain.entities.Teacher;
import com.mergingtonhigh.schoolmanagement.domain.repositories.ActivityRepository;
import com.mergingtonhigh.schoolmanagement.domain.repositories.TeacherRepository;
import com.mergingtonhigh.schoolmanagement.domain.valueobjects.Email;

@Service
public class StudentRegistrationUseCase {

    private final ActivityRepository activityRepository;
    private final TeacherRepository teacherRepository;

    public StudentRegistrationUseCase(ActivityRepository activityRepository, TeacherRepository teacherRepository) {
        this.activityRepository = activityRepository;
        this.teacherRepository = teacherRepository;
    }

    public String signupForActivity(String activityName, String email, String teacherUsername) {
        Teacher teacher = teacherRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new IllegalArgumentException("Credenciais de professor inválidas"));

        Activity activity = activityRepository.findByName(activityName)
                .orElseThrow(() -> new IllegalArgumentException("Atividade não encontrada"));

        validateTeacherAuthorization(teacher, activity);

        Email studentEmail = new Email(email);
        activity.addParticipant(studentEmail);

        activityRepository.save(activity);

        return String.format("Inscreveu %s em %s", email, activityName);
    }

    public String unregisterFromActivity(String activityName, String email, String teacherUsername) {
        Teacher teacher = teacherRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new IllegalArgumentException("Credenciais de professor inválidas"));

        Activity activity = activityRepository.findByName(activityName)
                .orElseThrow(() -> new IllegalArgumentException("Atividade não encontrada"));

        validateTeacherAuthorization(teacher, activity);

        Email studentEmail = new Email(email);
        activity.removeParticipant(studentEmail);

        activityRepository.save(activity);

        return String.format("Desinscreveu %s de %s", email, activityName);
    }

    private void validateTeacherAuthorization(Teacher teacher, Activity activity) {
        if (teacher.isAdmin()) {
            return;
        }

        if (!activity.isTeacherAssigned(teacher.getUsername())) {
            throw new IllegalArgumentException(
                    "Professor não autorizado a modificar esta atividade. Apenas professores vinculados à atividade podem fazer alterações.");
        }
    }
}