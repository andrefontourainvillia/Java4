package com.mergingtonhigh.schoolmanagement.application.usecases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mergingtonhigh.schoolmanagement.domain.entities.Activity;
import com.mergingtonhigh.schoolmanagement.domain.entities.Teacher;
import com.mergingtonhigh.schoolmanagement.domain.exceptions.AuthenticationException;
import com.mergingtonhigh.schoolmanagement.domain.exceptions.AuthorizationException;
import com.mergingtonhigh.schoolmanagement.domain.exceptions.NotFoundException;
import com.mergingtonhigh.schoolmanagement.domain.repositories.ActivityRepository;
import com.mergingtonhigh.schoolmanagement.domain.repositories.TeacherRepository;
import com.mergingtonhigh.schoolmanagement.domain.valueobjects.Email;

@Service
public class StudentRegistrationUseCase {

    private static final Logger logger = LoggerFactory.getLogger(StudentRegistrationUseCase.class);

    private final ActivityRepository activityRepository;
    private final TeacherRepository teacherRepository;

    public StudentRegistrationUseCase(ActivityRepository activityRepository, TeacherRepository teacherRepository) {
        this.activityRepository = activityRepository;
        this.teacherRepository = teacherRepository;
    }

    public String signupForActivity(String activityName, String email, String teacherUsername) {
        logger.debug("Attempting to signup {} for activity {} by teacher {}", email, activityName, teacherUsername);
        
        Teacher teacher = teacherRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> {
                    logger.warn("Invalid teacher credentials for signup: {}", teacherUsername);
                    return new AuthenticationException("Credenciais de professor inválidas");
                });

        Activity activity = activityRepository.findByName(activityName)
                .orElseThrow(() -> {
                    logger.warn("Activity not found for signup: {}", activityName);
                    return new NotFoundException("Atividade não encontrada");
                });

        validateTeacherAuthorization(teacher, activity);

        Email studentEmail = new Email(email);
        activity.addParticipant(studentEmail);

        activityRepository.save(activity);

        logger.info("Successfully signed up {} for activity {} by teacher {}", email, activityName, teacherUsername);
        return String.format("Inscreveu %s em %s", email, activityName);
    }

    public String unregisterFromActivity(String activityName, String email, String teacherUsername) {
        logger.debug("Attempting to unregister {} from activity {} by teacher {}", email, activityName, teacherUsername);
        
        Teacher teacher = teacherRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> {
                    logger.warn("Invalid teacher credentials for unregistration: {}", teacherUsername);
                    return new AuthenticationException("Credenciais de professor inválidas");
                });

        Activity activity = activityRepository.findByName(activityName)
                .orElseThrow(() -> {
                    logger.warn("Activity not found for unregistration: {}", activityName);
                    return new NotFoundException("Atividade não encontrada");
                });

        validateTeacherAuthorization(teacher, activity);

        Email studentEmail = new Email(email);
        activity.removeParticipant(studentEmail);

        activityRepository.save(activity);

        logger.info("Successfully unregistered {} from activity {} by teacher {}", email, activityName, teacherUsername);
        return String.format("Desinscreveu %s de %s", email, activityName);
    }

    private void validateTeacherAuthorization(Teacher teacher, Activity activity) {
        if (teacher.isAdmin()) {
            logger.debug("Admin teacher {} authorized for activity {}", teacher.getUsername(), activity.getName());
            return;
        }

        if (!activity.canTeachersRegisterStudents()) {
            logger.warn("Teacher {} not authorized for activity {} - teachers cannot register students", teacher.getUsername(), activity.getName());
            throw new AuthorizationException(
                    "Professores não podem registrar estudantes nesta atividade. Apenas administradores podem fazer alterações.");
        }
        
        logger.debug("Teacher {} authorized for activity {}", teacher.getUsername(), activity.getName());
    }
}