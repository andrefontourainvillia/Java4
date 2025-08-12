package com.mergingtonhigh.schoolmanagement.domain.entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mergingtonhigh.schoolmanagement.domain.valueobjects.CategoryReference;
import com.mergingtonhigh.schoolmanagement.domain.valueobjects.Email;
import com.mergingtonhigh.schoolmanagement.domain.valueobjects.ScheduleDetails;
import com.mergingtonhigh.schoolmanagement.domain.valueobjects.TeacherReference;

@Document(collection = "activities")
public class Activity {

    @Id
    private String name;
    private String description;
    private ScheduleDetails scheduleDetails;
    private int maxParticipants;
    private List<String> participantEmails;
    private List<TeacherReference> assignedTeachers;
    private CategoryReference category;
    private String categoryId;
    private List<String> assignedTeacherUsernames;

    public Activity() {
        this.participantEmails = new ArrayList<>();
        this.assignedTeachers = new ArrayList<>();
        this.assignedTeacherUsernames = new ArrayList<>();
    }

    public Activity(String name, String description, ScheduleDetails scheduleDetails,
            int maxParticipants, String categoryId) {
        this.name = validateName(name);
        this.description = validateDescription(description);
        this.scheduleDetails = scheduleDetails;
        this.maxParticipants = validateMaxParticipants(maxParticipants);
        this.categoryId = categoryId;
        this.participantEmails = new ArrayList<>();
        this.assignedTeachers = new ArrayList<>();
        this.assignedTeacherUsernames = new ArrayList<>();
    }

    // Construtor adicional para incluir dados embarcados completos
    public Activity(String name, String description, ScheduleDetails scheduleDetails,
            int maxParticipants, CategoryReference category) {
        this.name = validateName(name);
        this.description = validateDescription(description);
        this.scheduleDetails = scheduleDetails;
        this.maxParticipants = validateMaxParticipants(maxParticipants);
        this.category = category;
        this.categoryId = category != null ? category.getId() : null;
        this.participantEmails = new ArrayList<>();
        this.assignedTeachers = new ArrayList<>();
        this.assignedTeacherUsernames = new ArrayList<>();
    }

    public boolean canAddParticipant() {
        return participantEmails.size() < maxParticipants;
    }

    public boolean isParticipantRegistered(Email email) {
        return participantEmails.contains(email.value());
    }

    public void addParticipant(Email email) {
        if (!canAddParticipant()) {
            throw new IllegalStateException("Atividade está na capacidade máxima");
        }
        if (isParticipantRegistered(email)) {
            throw new IllegalArgumentException("Estudante já está inscrito nesta atividade");
        }
        participantEmails.add(email.value());
    }

    public void removeParticipant(Email email) {
        if (!isParticipantRegistered(email)) {
            throw new IllegalArgumentException("Estudante não está inscrito nesta atividade");
        }
        participantEmails.remove(email.value());
    }

    public boolean isTeacherAssigned(String teacherUsername) {
        return assignedTeacherUsernames.contains(teacherUsername);
    }

    public void assignTeacher(String teacherUsername) {
        if (teacherUsername == null || teacherUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome de usuário do professor não pode ser nulo ou vazio");
        }
        if (!isTeacherAssigned(teacherUsername)) {
            assignedTeacherUsernames.add(teacherUsername.trim());
        }
    }

    // Método para atribuir professor com dados completos embarcados
    public void assignTeacher(TeacherReference teacher) {
        if (teacher == null) {
            throw new IllegalArgumentException("Professor não pode ser nulo");
        }

        String username = teacher.getUsername();
        if (!isTeacherAssigned(username)) {
            assignedTeacherUsernames.add(username);
            assignedTeachers.removeIf(t -> t.getUsername().equals(username)); // Remove se já existir
            assignedTeachers.add(teacher);
        }
    }

    public void removeTeacher(String teacherUsername) {
        assignedTeacherUsernames.remove(teacherUsername);
        assignedTeachers.removeIf(t -> t.getUsername().equals(teacherUsername));
    }

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da atividade não pode ser nulo ou vazio");
        }
        return name.trim();
    }

    private String validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição da atividade não pode ser nula ou vazia");
        }
        return description.trim();
    }

    private int validateMaxParticipants(int maxParticipants) {
        if (maxParticipants <= 0) {
            throw new IllegalArgumentException("Número máximo de participantes deve ser maior que 0");
        }
        return maxParticipants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = validateName(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = validateDescription(description);
    }

    public ScheduleDetails getScheduleDetails() {
        return scheduleDetails;
    }

    public void setScheduleDetails(ScheduleDetails scheduleDetails) {
        this.scheduleDetails = scheduleDetails;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = validateMaxParticipants(maxParticipants);
    }

    public List<String> getParticipants() {
        return new ArrayList<>(participantEmails);
    }

    public void setParticipants(List<String> participants) {
        this.participantEmails = participants != null ? new ArrayList<>(participants) : new ArrayList<>();
    }

    public int getCurrentParticipantCount() {
        return participantEmails.size();
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getAssignedTeachers() {
        return new ArrayList<>(assignedTeacherUsernames);
    }

    public void setAssignedTeachers(List<String> assignedTeachers) {
        this.assignedTeacherUsernames = assignedTeachers != null ? new ArrayList<>(assignedTeachers)
                : new ArrayList<>();
    }

    // Novos métodos para trabalhar com dados embarcados
    public List<TeacherReference> getAssignedTeacherReferences() {
        return new ArrayList<>(assignedTeachers);
    }

    public void setAssignedTeacherReferences(List<TeacherReference> assignedTeachers) {
        this.assignedTeachers = assignedTeachers != null ? new ArrayList<>(assignedTeachers) : new ArrayList<>();
        this.assignedTeacherUsernames = this.assignedTeachers.stream()
                .map(TeacherReference::getUsername)
                .collect(java.util.stream.Collectors.toList());
    }

    public CategoryReference getCategory() {
        return category;
    }

    public void setCategory(CategoryReference category) {
        this.category = category;
        this.categoryId = category != null ? category.getId() : null;
    }

    public List<String> getAssignedTeacherUsernames() {
        return new ArrayList<>(assignedTeacherUsernames);
    }

    public int getRemainingSpots() {
        return Math.max(0, maxParticipants - participantEmails.size());
    }

    public boolean isFull() {
        return participantEmails.size() >= maxParticipants;
    }

    public int getCapacityUtilizationPercentage() {
        if (maxParticipants == 0) {
            return 100;
        }
        return (participantEmails.size() * 100) / maxParticipants;
    }

    @Override
    public String toString() {
        return String.format("Activity{name='%s', participants=%d/%d, category='%s'}",
                name,
                participantEmails.size(),
                maxParticipants,
                categoryId);
    }
}