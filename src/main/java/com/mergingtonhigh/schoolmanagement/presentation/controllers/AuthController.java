package com.mergingtonhigh.schoolmanagement.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mergingtonhigh.schoolmanagement.application.dtos.TeacherDTO;
import com.mergingtonhigh.schoolmanagement.application.usecases.AuthenticationUseCase;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationUseCase authenticationUseCase;

    public AuthController(AuthenticationUseCase authenticationUseCase) {
        this.authenticationUseCase = authenticationUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<TeacherDTO> login(@RequestParam String username, @RequestParam String password) {
        TeacherDTO teacher = authenticationUseCase.login(username, password);
        return ResponseEntity.ok(teacher);
    }

    @GetMapping("/check-session")
    public ResponseEntity<TeacherDTO> checkSession(@RequestParam String username) {
        TeacherDTO teacher = authenticationUseCase.checkSession(username);
        return ResponseEntity.ok(teacher);
    }
}