package com.mergingtonhigh.schoolmanagement.domain.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TeacherTest {

    @Test
    void shouldCreateTeacherWithValidData() {
        Teacher teacher = new Teacher("teacher1", "Teacher One", "password123", Teacher.Role.TEACHER);

        assertEquals("teacher1", teacher.getUsername());
        assertEquals("Teacher One", teacher.getDisplayName());
        assertEquals("password123", teacher.getPassword());
        assertEquals(Teacher.Role.TEACHER, teacher.getRole());
        assertFalse(teacher.isAdmin());
    }

    @Test
    void shouldCreateAdminTeacher() {
        Teacher teacher = new Teacher("admin1", "Admin One", "password123", Teacher.Role.ADMIN);

        assertEquals("admin1", teacher.getUsername());
        assertEquals("Admin One", teacher.getDisplayName());
        assertEquals("password123", teacher.getPassword());
        assertEquals(Teacher.Role.ADMIN, teacher.getRole());
        assertTrue(teacher.isAdmin());
    }

    @Test
    void shouldDefaultToTeacherRoleWhenRoleIsNull() {
        Teacher teacher = new Teacher("teacher1", "Teacher One", "password123", null);

        assertEquals(Teacher.Role.TEACHER, teacher.getRole());
        assertFalse(teacher.isAdmin());
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Teacher(null, "Teacher One", "password123", Teacher.Role.TEACHER));
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> new Teacher("   ", "Teacher One", "password123", Teacher.Role.TEACHER));
    }

    @Test
    void shouldThrowExceptionWhenDisplayNameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Teacher("teacher1", null, "password123", Teacher.Role.TEACHER));
    }

    @Test
    void shouldThrowExceptionWhenDisplayNameIsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> new Teacher("teacher1", "   ", "password123", Teacher.Role.TEACHER));
    }

    @Test
    void shouldTrimUsernameAndDisplayName() {
        Teacher teacher = new Teacher("  teacher1  ", "  Teacher One  ", "password123", Teacher.Role.TEACHER);

        assertEquals("teacher1", teacher.getUsername());
        assertEquals("Teacher One", teacher.getDisplayName());
    }

    @Test
    void shouldSetUsernameWithValidation() {
        Teacher teacher = new Teacher("teacher1", "Teacher One", "password123", Teacher.Role.TEACHER);

        teacher.setUsername("  newusername  ");

        assertEquals("newusername", teacher.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenSettingNullUsername() {
        Teacher teacher = new Teacher("teacher1", "Teacher One", "password123", Teacher.Role.TEACHER);

        assertThrows(IllegalArgumentException.class,
                () -> teacher.setUsername(null));
    }

    @Test
    void shouldThrowExceptionWhenSettingEmptyUsername() {
        Teacher teacher = new Teacher("teacher1", "Teacher One", "password123", Teacher.Role.TEACHER);

        assertThrows(IllegalArgumentException.class,
                () -> teacher.setUsername("   "));
    }

    @Test
    void shouldSetDisplayNameWithValidation() {
        Teacher teacher = new Teacher("teacher1", "Teacher One", "password123", Teacher.Role.TEACHER);

        teacher.setDisplayName("  New Display Name  ");

        assertEquals("New Display Name", teacher.getDisplayName());
    }

    @Test
    void shouldThrowExceptionWhenSettingNullDisplayName() {
        Teacher teacher = new Teacher("teacher1", "Teacher One", "password123", Teacher.Role.TEACHER);

        assertThrows(IllegalArgumentException.class,
                () -> teacher.setDisplayName(null));
    }

    @Test
    void shouldThrowExceptionWhenSettingEmptyDisplayName() {
        Teacher teacher = new Teacher("teacher1", "Teacher One", "password123", Teacher.Role.TEACHER);

        assertThrows(IllegalArgumentException.class,
                () -> teacher.setDisplayName("   "));
    }

    @Test
    void shouldSetPassword() {
        Teacher teacher = new Teacher("teacher1", "Teacher One", "password123", Teacher.Role.TEACHER);

        teacher.setPassword("newpassword");

        assertEquals("newpassword", teacher.getPassword());
    }

    @Test
    void shouldSetRole() {
        Teacher teacher = new Teacher("teacher1", "Teacher One", "password123", Teacher.Role.TEACHER);

        teacher.setRole(Teacher.Role.ADMIN);

        assertEquals(Teacher.Role.ADMIN, teacher.getRole());
        assertTrue(teacher.isAdmin());
    }

    @Test
    void shouldDefaultToTeacherRoleWhenSettingNullRole() {
        Teacher teacher = new Teacher("teacher1", "Teacher One", "password123", Teacher.Role.ADMIN);

        teacher.setRole(null);

        assertEquals(Teacher.Role.TEACHER, teacher.getRole());
        assertFalse(teacher.isAdmin());
    }

    @Test
    void shouldCreateEmptyTeacherWithDefaultConstructor() {
        Teacher teacher = new Teacher();

        // Default constructor should work for framework usage
        // but we can't really assert much since fields will be null
    }
}