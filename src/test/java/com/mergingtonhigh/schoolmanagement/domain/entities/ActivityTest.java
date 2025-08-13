package com.mergingtonhigh.schoolmanagement.domain.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.mergingtonhigh.schoolmanagement.domain.valueobjects.DifficultyLevel;
import com.mergingtonhigh.schoolmanagement.domain.valueobjects.Email;
import com.mergingtonhigh.schoolmanagement.domain.valueobjects.ScheduleDetails;

class ActivityTest {

    @Test
    void shouldCreateActivityWithValidData() {
        ScheduleDetails schedule = new ScheduleDetails(
                List.of("Monday", "Wednesday"),
                LocalTime.of(15, 30),
                LocalTime.of(17, 0));

        Activity activity = new Activity(
                "Clube de Xadrez",
                "Aprenda estratégias de xadrez",
                schedule,
                12,
                "academic");

        assertEquals("Clube de Xadrez", activity.getName());
        assertEquals("Aprenda estratégias de xadrez", activity.getDescription());
        assertEquals(12, activity.getMaxParticipants());
        assertEquals(0, activity.getCurrentParticipantCount());
        assertTrue(activity.canAddParticipant());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        ScheduleDetails schedule = new ScheduleDetails(
                List.of("Monday"),
                LocalTime.of(15, 30),
                LocalTime.of(17, 0));

        assertThrows(IllegalArgumentException.class,
                () -> new Activity(null, "Description", schedule, 12, "academic"));
    }

    @Test
    void shouldAddParticipantSuccessfully() {
        Activity activity = createTestActivity();
        Email studentEmail = new Email("student@mergington.edu");

        activity.addParticipant(studentEmail);

        assertEquals(1, activity.getCurrentParticipantCount());
        assertTrue(activity.isParticipantRegistered(studentEmail));
    }

    @Test
    void shouldThrowExceptionWhenAddingDuplicateParticipant() {
        Activity activity = createTestActivity();
        Email studentEmail = new Email("student@mergington.edu");
        activity.addParticipant(studentEmail);

        assertThrows(IllegalArgumentException.class, () -> activity.addParticipant(studentEmail));
    }

    @Test
    void shouldRemoveParticipantSuccessfully() {
        Activity activity = createTestActivity();
        Email studentEmail = new Email("student@mergington.edu");
        activity.addParticipant(studentEmail);

        activity.removeParticipant(studentEmail);

        assertEquals(0, activity.getCurrentParticipantCount());
        assertFalse(activity.isParticipantRegistered(studentEmail));
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonExistentParticipant() {
        Activity activity = createTestActivity();
        Email studentEmail = new Email("student@mergington.edu");

        assertThrows(IllegalArgumentException.class, () -> activity.removeParticipant(studentEmail));
    }

    @Test
    void shouldAssignTeacherSuccessfully() {
        Activity activity = createTestActivity();
        String teacherUsername = "teacher1";

        activity.assignTeacher(teacherUsername);

        assertTrue(activity.isTeacherAssigned(teacherUsername));
        assertEquals(1, activity.getAssignedTeachers().size());
        assertTrue(activity.getAssignedTeachers().contains(teacherUsername));
    }

    @Test
    void shouldNotAddDuplicateTeacher() {
        Activity activity = createTestActivity();
        String teacherUsername = "teacher1";

        activity.assignTeacher(teacherUsername);
        activity.assignTeacher(teacherUsername);

        assertEquals(1, activity.getAssignedTeachers().size());
        assertTrue(activity.isTeacherAssigned(teacherUsername));
    }

    @Test
    void shouldRemoveTeacherSuccessfully() {
        Activity activity = createTestActivity();
        String teacherUsername = "teacher1";
        activity.assignTeacher(teacherUsername);

        activity.removeTeacher(teacherUsername);

        assertFalse(activity.isTeacherAssigned(teacherUsername));
        assertEquals(0, activity.getAssignedTeachers().size());
    }

    @Test
    void shouldThrowExceptionWhenAssigningNullTeacher() {
        Activity activity = createTestActivity();

        assertThrows(IllegalArgumentException.class, () -> activity.assignTeacher((String) null));
    }

    @Test
    void shouldThrowExceptionWhenAssigningEmptyTeacherUsername() {
        Activity activity = createTestActivity();

        assertThrows(IllegalArgumentException.class, () -> activity.assignTeacher(""));
        assertThrows(IllegalArgumentException.class, () -> activity.assignTeacher("   "));
    }

    @Test
    void shouldReturnCorrectCapacityInformation() {
        Activity activity = createTestActivity();
        Email student1 = new Email("student1@mergington.edu");
        Email student2 = new Email("student2@mergington.edu");

        assertEquals(12, activity.getRemainingSpots());
        assertFalse(activity.isFull());
        assertEquals(0, activity.getCapacityUtilizationPercentage());

        activity.addParticipant(student1);
        activity.addParticipant(student2);

        assertEquals(10, activity.getRemainingSpots());
        assertFalse(activity.isFull());
        assertEquals(16, activity.getCapacityUtilizationPercentage());
    }

    private Activity createTestActivity() {
        ScheduleDetails schedule = new ScheduleDetails(
                List.of("Monday"),
                LocalTime.of(15, 30),
                LocalTime.of(17, 0));

        return new Activity(
                "Test Activity",
                "Test Description",
                schedule,
                12,
                "academic");
    }

    @Test
    void shouldCreateActivityWithDifficultyLevel() {
        ScheduleDetails schedule = new ScheduleDetails(
                List.of("Monday"),
                LocalTime.of(15, 30),
                LocalTime.of(17, 0));

        Activity activity = new Activity(
                "Clube de Xadrez Avançado",
                "Xadrez para jogadores experientes",
                schedule,
                8,
                "academic",
                DifficultyLevel.AVANCADO);

        assertEquals("Clube de Xadrez Avançado", activity.getName());
        assertEquals(DifficultyLevel.AVANCADO, activity.getDifficultyLevel());
        assertFalse(activity.isForAllLevels());
    }

    @Test
    void shouldCreateActivityWithoutDifficultyLevel() {
        ScheduleDetails schedule = new ScheduleDetails(
                List.of("Monday"),
                LocalTime.of(15, 30),
                LocalTime.of(17, 0));

        Activity activity = new Activity(
                "Clube de Xadrez",
                "Xadrez para todos os níveis",
                schedule,
                12,
                "academic");

        assertEquals("Clube de Xadrez", activity.getName());
        assertEquals(null, activity.getDifficultyLevel());
        assertTrue(activity.isForAllLevels());
    }

    @Test
    void shouldSetAndGetDifficultyLevel() {
        Activity activity = createTestActivity();
        
        assertTrue(activity.isForAllLevels());
        
        activity.setDifficultyLevel(DifficultyLevel.INTERMEDIARIO);
        assertEquals(DifficultyLevel.INTERMEDIARIO, activity.getDifficultyLevel());
        assertFalse(activity.isForAllLevels());
        
        activity.setDifficultyLevel(null);
        assertTrue(activity.isForAllLevels());
    }
}