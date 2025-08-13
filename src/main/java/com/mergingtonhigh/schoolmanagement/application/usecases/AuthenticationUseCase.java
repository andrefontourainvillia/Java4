package com.mergingtonhigh.schoolmanagement.application.usecases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mergingtonhigh.schoolmanagement.application.dtos.TeacherDTO;
import com.mergingtonhigh.schoolmanagement.domain.entities.Teacher;
import com.mergingtonhigh.schoolmanagement.domain.exceptions.AuthenticationException;
import com.mergingtonhigh.schoolmanagement.domain.exceptions.NotFoundException;
import com.mergingtonhigh.schoolmanagement.domain.repositories.TeacherRepository;
import com.mergingtonhigh.schoolmanagement.presentation.mappers.TeacherMapper;

@Service
public class AuthenticationUseCase {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationUseCase.class);

    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeacherMapper teacherMapper;

    public AuthenticationUseCase(TeacherRepository teacherRepository,
            PasswordEncoder passwordEncoder,
            TeacherMapper teacherMapper) {
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
        this.teacherMapper = teacherMapper;
    }

    public TeacherDTO login(String username, String password) {
        logger.debug("Attempting login for username: {}", username);
        
        Teacher teacher = teacherRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Login failed - teacher not found: {}", username);
                    return new AuthenticationException("Usuário ou senha inválidos");
                });

        if (!passwordEncoder.matches(password, teacher.getPassword())) {
            logger.warn("Login failed - invalid password for user: {}", username);
            throw new AuthenticationException("Usuário ou senha inválidos");
        }

        logger.info("Successful login for user: {}", username);
        return teacherMapper.toDTO(teacher);
    }

    public TeacherDTO checkSession(String username) {
        logger.debug("Checking session for username: {}", username);
        
        Teacher teacher = teacherRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.debug("Session check failed - teacher not found: {}", username);
                    return new NotFoundException("Professor não encontrado");
                });

        return teacherMapper.toDTO(teacher);
    }
}