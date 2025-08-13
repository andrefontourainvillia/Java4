package com.mergingtonhigh.schoolmanagement.presentation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.mergingtonhigh.schoolmanagement.application.dtos.ActivityDTO;
import com.mergingtonhigh.schoolmanagement.application.usecases.ActivityUseCase;
import com.mergingtonhigh.schoolmanagement.application.usecases.StudentRegistrationUseCase;
import com.mergingtonhigh.schoolmanagement.domain.enums.ActivityCategory;
import com.mergingtonhigh.schoolmanagement.domain.exceptions.AuthenticationException;
import com.mergingtonhigh.schoolmanagement.domain.exceptions.NotFoundException;

@WebMvcTest(ActivityController.class)
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityUseCase activityUseCase;

    @MockBean
    private StudentRegistrationUseCase studentRegistrationUseCase;

    @Test
    void shouldReturnActivitiesSuccessfully() throws Exception {
        ActivityDTO.ScheduleDetailsDTO scheduleDTO = new ActivityDTO.ScheduleDetailsDTO(
                Arrays.asList("Monday"), "15:30", "17:00");
        
        ActivityDTO activityDTO = new ActivityDTO(
                "Chess Club",
                "Learn chess strategies",
                "Monday 15:30-17:00",
                scheduleDTO,
                12,
                Arrays.asList(),
                0,
                ActivityCategory.ACADEMIC,
                true);

        Map<String, ActivityDTO> activities = new HashMap<>();
        activities.put("Chess Club", activityDTO);

        when(activityUseCase.getActivities(null, null, null, null)).thenReturn(activities);

        mockMvc.perform(get("/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Chess Club'].name").value("Chess Club"))
                .andExpect(jsonPath("$['Chess Club'].description").value("Learn chess strategies"));
    }

    @Test
    void shouldReturnActivitiesWithFilters() throws Exception {
        ActivityDTO.ScheduleDetailsDTO scheduleDTO = new ActivityDTO.ScheduleDetailsDTO(
                Arrays.asList("Monday"), "15:30", "17:00");
        
        ActivityDTO activityDTO = new ActivityDTO(
                "Chess Club",
                "Learn chess strategies",
                "Monday 15:30-17:00",
                scheduleDTO,
                12,
                Arrays.asList(),
                0,
                ActivityCategory.ACADEMIC,
                true);

        Map<String, ActivityDTO> activities = new HashMap<>();
        activities.put("Chess Club", activityDTO);

        when(activityUseCase.getActivities("Monday", "15:00", "18:00", "ACADEMIC")).thenReturn(activities);

        mockMvc.perform(get("/activities")
                .param("day", "Monday")
                .param("start_time", "15:00")
                .param("end_time", "18:00")
                .param("category", "ACADEMIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Chess Club'].name").value("Chess Club"));
    }

    @Test
    void shouldReturnAvailableDays() throws Exception {
        when(activityUseCase.getAvailableDays()).thenReturn(Arrays.asList("Monday", "Wednesday", "Friday"));

        mockMvc.perform(get("/activities/days"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Monday"))
                .andExpect(jsonPath("$[1]").value("Wednesday"))
                .andExpect(jsonPath("$[2]").value("Friday"));
    }

    @Test
    void shouldSignupForActivitySuccessfully() throws Exception {
        when(studentRegistrationUseCase.signupForActivity("Chess Club", "student@test.com", "teacher1"))
                .thenReturn("Inscreveu student@test.com em Chess Club");

        mockMvc.perform(post("/activities/Chess Club/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("student_email", "student@test.com")
                .param("teacher_username", "teacher1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Inscreveu student@test.com em Chess Club"));
    }

    @Test
    void shouldReturnUnauthorizedWhenSignupFails() throws Exception {
        when(studentRegistrationUseCase.signupForActivity(eq("Chess Club"), eq("student@test.com"), eq("invalidteacher")))
                .thenThrow(new AuthenticationException("Usuário ou senha inválidos"));

        mockMvc.perform(post("/activities/Chess Club/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("student_email", "student@test.com")
                .param("teacher_username", "invalidteacher"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Usuário ou senha inválidos"));
    }

    @Test
    void shouldReturnNotFoundWhenActivityDoesNotExist() throws Exception {
        when(studentRegistrationUseCase.signupForActivity(eq("Nonexistent Activity"), eq("student@test.com"), eq("teacher1")))
                .thenThrow(new NotFoundException("Atividade não encontrada"));

        mockMvc.perform(post("/activities/Nonexistent Activity/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("student_email", "student@test.com")
                .param("teacher_username", "teacher1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Atividade não encontrada"));
    }

    @Test
    void shouldUnregisterFromActivitySuccessfully() throws Exception {
        when(studentRegistrationUseCase.unregisterFromActivity("Chess Club", "student@test.com", "teacher1"))
                .thenReturn("Desinscreveu student@test.com de Chess Club");

        mockMvc.perform(post("/activities/Chess Club/unregister")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("student_email", "student@test.com")
                .param("teacher_username", "teacher1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Desinscreveu student@test.com de Chess Club"));
    }
}