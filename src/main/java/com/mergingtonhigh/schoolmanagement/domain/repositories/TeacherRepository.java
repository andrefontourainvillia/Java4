package com.mergingtonhigh.schoolmanagement.domain.repositories;

import com.mergingtonhigh.schoolmanagement.domain.entities.Teacher;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository {
    
    List<Teacher> findAll();
    
    Optional<Teacher> findByUsername(String username);
    
    Teacher save(Teacher teacher);
    
    void deleteByUsername(String username);
    
    boolean existsByUsername(String username);
}