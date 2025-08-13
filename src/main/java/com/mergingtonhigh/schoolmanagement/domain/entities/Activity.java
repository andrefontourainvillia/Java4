package com.mergingtonhigh.schoolmanagement.domain.entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mergingtonhigh.schoolmanagement.domain.enums.ActivityCategory;
import com.mergingtonhigh.schoolmanagement.domain.valueobjects.Email;
import com.mergingtonhigh.schoolmanagement.domain.valueobjects.ScheduleDetails;

@Document(collection = "activities")
public class Activity {

    @Id
    private String name;
    private String description;
    private ScheduleDetails scheduleDetails;
    private int maxParticipants;
    private List<String> participantEmails;
    private ActivityCategory category;
    private boolean canTeachersRegisterStudents;

    public Activity() {
        this.participantEmails = new ArrayList<>();
        this.canTeachersRegisterStudents = true;
    }

    public Activity(String name, String description, ScheduleDetails scheduleDetails,
            int maxParticipants, ActivityCategory category) {
        this.name = validateName(name);
        this.description = validateDescription(description);
        this.scheduleDetails = scheduleDetails;
        this.maxParticipants = validateMaxParticipants(maxParticipants);
        this.category = category;
        this.participantEmails = new ArrayList<>();
        this.canTeachersRegisterStudents = true;
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

    public ActivityCategory getCategory() {
        return category;
    }

    public void setCategory(ActivityCategory category) {
        this.category = category;
    }

    public boolean canTeachersRegisterStudents() {
        return canTeachersRegisterStudents;
    }

    public void setCanTeachersRegisterStudents(boolean canTeachersRegisterStudents) {
        this.canTeachersRegisterStudents = canTeachersRegisterStudents;
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
                category != null ? category.getDisplayName() : "N/A");
    }
}