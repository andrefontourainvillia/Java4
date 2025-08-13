package com.mergingtonhigh.schoolmanagement.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mergingtonhigh.schoolmanagement.application.dtos.TeacherDTO;
import com.mergingtonhigh.schoolmanagement.application.mappers.TeacherMapper;
import com.mergingtonhigh.schoolmanagement.domain.entities.Teacher;
import com.mergingtonhigh.schoolmanagement.domain.exceptions.AuthenticationException;
import com.mergingtonhigh.schoolmanagement.domain.exceptions.NotFoundException;
import com.mergingtonhigh.schoolmanagement.domain.repositories.TeacherRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticationUseCaseTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TeacherMapper teacherMapper;

    private AuthenticationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AuthenticationUseCase(teacherRepository, passwordEncoder, teacherMapper);
    }

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        String username = "teacher1";
        String password = "password123";
        String encodedPassword = "encoded_password";

        Teacher teacher = new Teacher(username, "Teacher One", encodedPassword, Teacher.Role.TEACHER);
        TeacherDTO expectedDTO = new TeacherDTO(username, "Teacher One", Teacher.Role.TEACHER.name());

        when(teacherRepository.findByUsername(username)).thenReturn(Optional.of(teacher));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(teacherMapper.toDTO(teacher)).thenReturn(expectedDTO);

        TeacherDTO result = useCase.login(username, password);

        assertEquals(expectedDTO, result);
    }

    @Test
    void shouldThrowAuthenticationExceptionWhenTeacherNotFound() {
        String username = "nonexistent";
        String password = "password123";

        when(teacherRepository.findByUsername(username)).thenReturn(Optional.empty());

        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> useCase.login(username, password));

        assertEquals("Usuário ou senha inválidos", exception.getMessage());
    }

    @Test
    void shouldThrowAuthenticationExceptionWhenPasswordDoesNotMatch() {
        String username = "teacher1";
        String password = "wrongpassword";
        String encodedPassword = "encoded_password";

        Teacher teacher = new Teacher(username, "Teacher One", encodedPassword, Teacher.Role.TEACHER);

        when(teacherRepository.findByUsername(username)).thenReturn(Optional.of(teacher));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> useCase.login(username, password));

        assertEquals("Usuário ou senha inválidos", exception.getMessage());
    }

    @Test
    void shouldCheckSessionSuccessfully() {
        String username = "teacher1";
        Teacher teacher = new Teacher(username, "Teacher One", "password", Teacher.Role.TEACHER);
        TeacherDTO expectedDTO = new TeacherDTO(username, "Teacher One", Teacher.Role.TEACHER.name());

        when(teacherRepository.findByUsername(username)).thenReturn(Optional.of(teacher));
        when(teacherMapper.toDTO(teacher)).thenReturn(expectedDTO);

        TeacherDTO result = useCase.checkSession(username);

        assertEquals(expectedDTO, result);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCheckingNonExistentSession() {
        String username = "nonexistent";

        when(teacherRepository.findByUsername(username)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> useCase.checkSession(username));

        assertEquals("Professor não encontrado", exception.getMessage());
    }
}