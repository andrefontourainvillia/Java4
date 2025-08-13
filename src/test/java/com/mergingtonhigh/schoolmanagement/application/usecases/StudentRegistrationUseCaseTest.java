package com.mergingtonhigh.schoolmanagement.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mergingtonhigh.schoolmanagement.domain.entities.Activity;
import com.mergingtonhigh.schoolmanagement.domain.entities.Teacher;
import com.mergingtonhigh.schoolmanagement.domain.enums.ActivityCategory;
import com.mergingtonhigh.schoolmanagement.domain.exceptions.AuthenticationException;
import com.mergingtonhigh.schoolmanagement.domain.exceptions.AuthorizationException;
import com.mergingtonhigh.schoolmanagement.domain.exceptions.NotFoundException;
import com.mergingtonhigh.schoolmanagement.domain.repositories.ActivityRepository;
import com.mergingtonhigh.schoolmanagement.domain.repositories.TeacherRepository;
import com.mergingtonhigh.schoolmanagement.domain.valueobjects.ScheduleDetails;

@ExtendWith(MockitoExtension.class)
class StudentRegistrationUseCaseTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private TeacherRepository teacherRepository;

    private StudentRegistrationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new StudentRegistrationUseCase(activityRepository, teacherRepository);
    }

    @Test
    void shouldSignupStudentForActivitySuccessfully() {
        String activityName = "Clube de Xadrez";
        String email = "student@mergington.edu";
        String teacherUsername = "teacher1";

        Teacher teacher = new Teacher(teacherUsername, "Teacher", "password", Teacher.Role.TEACHER);
        Activity activity = createTestActivity(activityName);
        activity.setCanTeachersRegisterStudents(true); // Allow teachers to register students

        when(teacherRepository.findByUsername(teacherUsername)).thenReturn(Optional.of(teacher));
        when(activityRepository.findByName(activityName)).thenReturn(Optional.of(activity));
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);

        String result = useCase.signupForActivity(activityName, email, teacherUsername);

        assertEquals("Inscreveu student@mergington.edu em Clube de Xadrez", result);
        verify(activityRepository).save(activity);
        assertTrue(activity.getParticipants().contains(email));
    }

    @Test
    void shouldThrowExceptionWhenTeacherNotFound() {
        String activityName = "Clube de Xadrez";
        String email = "student@mergington.edu";
        String teacherUsername = "nonexistent";

        when(teacherRepository.findByUsername(teacherUsername)).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class,
                () -> useCase.signupForActivity(activityName, email, teacherUsername));

        verify(activityRepository, never()).findByName(any());
        verify(activityRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenActivityNotFound() {
        String activityName = "Nonexistent Activity";
        String email = "student@mergington.edu";
        String teacherUsername = "teacher1";

        Teacher teacher = new Teacher(teacherUsername, "Teacher", "password", Teacher.Role.TEACHER);

        when(teacherRepository.findByUsername(teacherUsername)).thenReturn(Optional.of(teacher));
        when(activityRepository.findByName(activityName)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> useCase.signupForActivity(activityName, email, teacherUsername));

        verify(activityRepository, never()).save(any());
    }

    @Test
    void shouldUnregisterStudentFromActivitySuccessfully() {
        String activityName = "Clube de Xadrez";
        String email = "student@mergington.edu";
        String teacherUsername = "teacher1";

        Teacher teacher = new Teacher(teacherUsername, "Teacher", "password", Teacher.Role.TEACHER);
        Activity activity = createTestActivity(activityName);
        activity.setCanTeachersRegisterStudents(true); // Allow teachers to register students
        activity.setParticipants(List.of(email));

        when(teacherRepository.findByUsername(teacherUsername)).thenReturn(Optional.of(teacher));
        when(activityRepository.findByName(activityName)).thenReturn(Optional.of(activity));
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);

        String result = useCase.unregisterFromActivity(activityName, email, teacherUsername);

        assertEquals("Desinscreveu student@mergington.edu de Clube de Xadrez", result);
        verify(activityRepository).save(activity);
        assertFalse(activity.getParticipants().contains(email));
    }

    @Test
    void shouldThrowExceptionWhenTeacherNotAuthorizedToModifyActivity() {
        String activityName = "Clube de Xadrez";
        String email = "student@mergington.edu";
        String teacherUsername = "unauthorizedTeacher";

        Teacher teacher = new Teacher(teacherUsername, "Unauthorized Teacher", "password", Teacher.Role.TEACHER);
        Activity activity = createTestActivity(activityName);
        activity.setCanTeachersRegisterStudents(false); // Don't allow teachers to register students

        when(teacherRepository.findByUsername(teacherUsername)).thenReturn(Optional.of(teacher));
        when(activityRepository.findByName(activityName)).thenReturn(Optional.of(activity));

        AuthorizationException exception = assertThrows(AuthorizationException.class,
                () -> useCase.signupForActivity(activityName, email, teacherUsername));

        assertEquals(
                "Professores não podem registrar estudantes nesta atividade. Apenas administradores podem fazer alterações.",
                exception.getMessage());
        verify(activityRepository, never()).save(any());
    }

    @Test
    void shouldAllowAdminToModifyAnyActivity() {
        String activityName = "Clube de Xadrez";
        String email = "student@mergington.edu";
        String adminUsername = "admin";

        Teacher admin = new Teacher(adminUsername, "Administrator", "password", Teacher.Role.ADMIN);
        Activity activity = createTestActivity(activityName);

        when(teacherRepository.findByUsername(adminUsername)).thenReturn(Optional.of(admin));
        when(activityRepository.findByName(activityName)).thenReturn(Optional.of(activity));
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);

        String result = useCase.signupForActivity(activityName, email, adminUsername);

        assertEquals("Inscreveu student@mergington.edu em Clube de Xadrez", result);
        verify(activityRepository).save(activity);
        assertTrue(activity.getParticipants().contains(email));
    }

    private Activity createTestActivity(String name) {
        ScheduleDetails schedule = new ScheduleDetails(
                List.of("Monday"),
                LocalTime.of(15, 30),
                LocalTime.of(17, 0));

        return new Activity(
                name,
                "Test Description",
                schedule,
                12,
                ActivityCategory.ACADEMIC);
    }
}