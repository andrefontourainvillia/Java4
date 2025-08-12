package com.mergingtonhigh.schoolmanagement.domain.valueobjects;

import com.mergingtonhigh.schoolmanagement.domain.entities.Teacher;

public class TeacherReference {

    private final String username;
    private final String displayName;
    private final Teacher.Role role;

    public TeacherReference(String username, String displayName, Teacher.Role role) {
        this.username = validateUsername(username);
        this.displayName = validateDisplayName(displayName);
        this.role = validateRole(role);
    }

    public static TeacherReference fromTeacher(Teacher teacher) {
        return new TeacherReference(
                teacher.getUsername(),
                teacher.getDisplayName(),
                teacher.getRole());
    }

    private String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username não pode ser nulo ou vazio");
        }
        return username.trim();
    }

    private String validateDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome de exibição não pode ser nulo ou vazio");
        }
        return displayName.trim();
    }

    private Teacher.Role validateRole(Teacher.Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role do professor não pode ser nulo");
        }
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Teacher.Role getRole() {
        return role;
    }

    public boolean isAdmin() {
        return role == Teacher.Role.ADMIN;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        TeacherReference that = (TeacherReference) obj;
        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return String.format("TeacherReference{username='%s', displayName='%s', role=%s}",
                username, displayName, role);
    }
}
