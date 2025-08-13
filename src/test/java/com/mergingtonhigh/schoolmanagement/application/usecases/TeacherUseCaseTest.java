package com.mergingtonhigh.schoolmanagement.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mergingtonhigh.schoolmanagement.application.dtos.TeacherDTO;
import com.mergingtonhigh.schoolmanagement.application.mappers.TeacherMapper;
import com.mergingtonhigh.schoolmanagement.domain.entities.Teacher;
import com.mergingtonhigh.schoolmanagement.domain.repositories.TeacherRepository;

@ExtendWith(MockitoExtension.class)
class TeacherUseCaseTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private TeacherMapper teacherMapper;

    private TeacherUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new TeacherUseCase(teacherRepository, teacherMapper);
    }

    @Test
    void shouldReturnAllTeachers() {
        Teacher teacher1 = new Teacher("teacher1", "Teacher One", "password", Teacher.Role.TEACHER);
        Teacher teacher2 = new Teacher("admin1", "Admin One", "password", Teacher.Role.ADMIN);
        List<Teacher> teachers = Arrays.asList(teacher1, teacher2);

        TeacherDTO dto1 = new TeacherDTO("teacher1", "Teacher One", Teacher.Role.TEACHER.name());
        TeacherDTO dto2 = new TeacherDTO("admin1", "Admin One", Teacher.Role.ADMIN.name());

        when(teacherRepository.findAll()).thenReturn(teachers);
        when(teacherMapper.toDTO(teacher1)).thenReturn(dto1);
        when(teacherMapper.toDTO(teacher2)).thenReturn(dto2);

        List<TeacherDTO> result = useCase.getAllTeachers();

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
    }

    @Test
    void shouldReturnTeacherByUsername() {
        String username = "teacher1";
        Teacher teacher = new Teacher(username, "Teacher One", "password", Teacher.Role.TEACHER);
        TeacherDTO expectedDTO = new TeacherDTO(username, "Teacher One", Teacher.Role.TEACHER.name());

        when(teacherRepository.findByUsername(username)).thenReturn(Optional.of(teacher));
        when(teacherMapper.toDTO(teacher)).thenReturn(expectedDTO);

        Optional<TeacherDTO> result = useCase.getTeacherByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(expectedDTO, result.get());
    }

    @Test
    void shouldReturnEmptyWhenTeacherNotFound() {
        String username = "nonexistent";

        when(teacherRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<TeacherDTO> result = useCase.getTeacherByUsername(username);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenUsernameIsNull() {
        Optional<TeacherDTO> result = useCase.getTeacherByUsername(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenUsernameIsEmpty() {
        Optional<TeacherDTO> result = useCase.getTeacherByUsername("   ");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCreateNewTeacher() {
        String username = "newteacher";
        String displayName = "New Teacher";
        String password = "password123";
        Teacher.Role role = Teacher.Role.TEACHER;

        Teacher teacher = new Teacher(username, displayName, password, role);
        TeacherDTO expectedDTO = new TeacherDTO(username, displayName, role.name());

        when(teacherRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);
        when(teacherMapper.toDTO(teacher)).thenReturn(expectedDTO);

        TeacherDTO result = useCase.saveTeacher(username, displayName, password, role);

        assertEquals(expectedDTO, result);
        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    void shouldUpdateExistingTeacher() {
        String username = "existingteacher";
        String newDisplayName = "Updated Teacher";
        String newPassword = "newpassword";
        Teacher.Role newRole = Teacher.Role.ADMIN;

        Teacher existingTeacher = new Teacher(username, "Old Name", "oldpassword", Teacher.Role.TEACHER);
        TeacherDTO expectedDTO = new TeacherDTO(username, newDisplayName, newRole.name());

        when(teacherRepository.findByUsername(username)).thenReturn(Optional.of(existingTeacher));
        when(teacherRepository.save(existingTeacher)).thenReturn(existingTeacher);
        when(teacherMapper.toDTO(existingTeacher)).thenReturn(expectedDTO);

        TeacherDTO result = useCase.saveTeacher(username, newDisplayName, newPassword, newRole);

        assertEquals(expectedDTO, result);
        verify(teacherRepository).save(existingTeacher);
    }

    @Test
    void shouldUpdateExistingTeacherWithoutPassword() {
        String username = "existingteacher";
        String newDisplayName = "Updated Teacher";
        Teacher.Role newRole = Teacher.Role.ADMIN;

        Teacher existingTeacher = new Teacher(username, "Old Name", "oldpassword", Teacher.Role.TEACHER);
        TeacherDTO expectedDTO = new TeacherDTO(username, newDisplayName, newRole.name());

        when(teacherRepository.findByUsername(username)).thenReturn(Optional.of(existingTeacher));
        when(teacherRepository.save(existingTeacher)).thenReturn(existingTeacher);
        when(teacherMapper.toDTO(existingTeacher)).thenReturn(expectedDTO);

        TeacherDTO result = useCase.saveTeacher(username, newDisplayName, null, newRole);

        assertEquals(expectedDTO, result);
        verify(teacherRepository).save(existingTeacher);
    }

    @Test
    void shouldThrowExceptionWhenSavingTeacherWithNullUsername() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.saveTeacher(null, "Display Name", "password", Teacher.Role.TEACHER));

        assertEquals("Username não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSavingTeacherWithEmptyUsername() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.saveTeacher("   ", "Display Name", "password", Teacher.Role.TEACHER));

        assertEquals("Username não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    void shouldDeleteTeacherSuccessfully() {
        String username = "teachertodelete";

        when(teacherRepository.existsByUsername(username)).thenReturn(true);

        useCase.deleteTeacher(username);

        verify(teacherRepository).deleteByUsername(username);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTeacher() {
        String username = "nonexistent";

        when(teacherRepository.existsByUsername(username)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.deleteTeacher(username));

        assertEquals("Professor não encontrado: " + username, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeletingWithNullUsername() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.deleteTeacher(null));

        assertEquals("Username inválido: null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeletingWithEmptyUsername() {
        String username = "   ";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.deleteTeacher(username));

        assertEquals("Username inválido: " + username, exception.getMessage());
    }
}