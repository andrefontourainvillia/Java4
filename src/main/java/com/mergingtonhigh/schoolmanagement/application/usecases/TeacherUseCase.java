package com.mergingtonhigh.schoolmanagement.application.usecases;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mergingtonhigh.schoolmanagement.application.dtos.TeacherDTO;
import com.mergingtonhigh.schoolmanagement.application.services.ActivitySyncService;
import com.mergingtonhigh.schoolmanagement.domain.entities.Teacher;
import com.mergingtonhigh.schoolmanagement.domain.repositories.TeacherRepository;
import com.mergingtonhigh.schoolmanagement.presentation.mappers.TeacherMapper;

@Service
public class TeacherUseCase {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final ActivitySyncService activitySyncService;

    public TeacherUseCase(TeacherRepository teacherRepository,
            TeacherMapper teacherMapper,
            ActivitySyncService activitySyncService) {
        this.teacherRepository = teacherRepository;
        this.teacherMapper = teacherMapper;
        this.activitySyncService = activitySyncService;
    }

    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(teacherMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<TeacherDTO> getTeacherByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }

        return teacherRepository.findByUsername(username)
                .map(teacherMapper::toDTO);
    }

    public TeacherDTO saveTeacher(String username, String displayName, String password, Teacher.Role role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username não pode ser nulo ou vazio");
        }

        Optional<Teacher> existingTeacher = teacherRepository.findByUsername(username);

        Teacher teacher;
        if (existingTeacher.isPresent()) {
            teacher = existingTeacher.get();
            teacher.setDisplayName(displayName);
            if (password != null && !password.trim().isEmpty()) {
                teacher.setPassword(password);
            }
            if (role != null) {
                teacher.setRole(role);
            }
        } else {
            teacher = new Teacher(username, displayName, password, role);
        }

        Teacher savedTeacher = teacherRepository.save(teacher);

        // Sincroniza dados embarcados em todas as atividades onde o professor está
        // atribuído
        activitySyncService.syncTeacherDataInActivities(savedTeacher);

        return teacherMapper.toDTO(savedTeacher);
    }

    public void deleteTeacher(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username inválido: " + username);
        }

        if (!teacherRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Professor não encontrado: " + username);
        }

        // Remove todas as referências embarcadas do professor das atividades
        activitySyncService.removeTeacherFromActivities(username);

        teacherRepository.deleteByUsername(username);
    }
}
