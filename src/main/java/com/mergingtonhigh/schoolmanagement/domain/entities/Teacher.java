package com.mergingtonhigh.schoolmanagement.domain.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "teachers")
public class Teacher {

    @Id
    private String username;
    private String displayName;
    private String password;
    private Role role;

    public enum Role {
        TEACHER, ADMIN
    }

    public Teacher() {
    }

    public Teacher(String username, String displayName, String password, Role role) {
        this.username = validateUsername(username);
        this.displayName = validateDisplayName(displayName);
        this.password = password;
        this.role = role != null ? role : Role.TEACHER;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    private String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome de usuário não pode ser nulo ou vazio");
        }
        return username.trim();
    }

    private String validateDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome de exibição não pode ser nulo ou vazio");
        }
        return displayName.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = validateUsername(username);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = validateDisplayName(displayName);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role != null ? role : Role.TEACHER;
    }
}