package com.mergingtonhigh.schoolmanagement.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mergingtonhigh.schoolmanagement.application.dtos.TeacherDTO;
import com.mergingtonhigh.schoolmanagement.application.usecases.AuthenticationUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Teacher authentication endpoints")
public class AuthController {

    private final AuthenticationUseCase authenticationUseCase;

    public AuthController(AuthenticationUseCase authenticationUseCase) {
        this.authenticationUseCase = authenticationUseCase;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate teacher", description = "Authenticate a teacher with username and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherDTO.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<TeacherDTO> login(
            @Parameter(description = "Teacher username", required = true) @RequestParam String username, 
            @Parameter(description = "Teacher password", required = true) @RequestParam String password) {
        TeacherDTO teacher = authenticationUseCase.login(username, password);
        return ResponseEntity.ok(teacher);
    }

    @GetMapping("/check-session")
    @Operation(summary = "Check session", description = "Validate if a teacher session is still active")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Session is valid",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherDTO.class))),
        @ApiResponse(responseCode = "404", description = "Teacher not found",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<TeacherDTO> checkSession(
            @Parameter(description = "Teacher username", required = true) @RequestParam String username) {
        TeacherDTO teacher = authenticationUseCase.checkSession(username);
        return ResponseEntity.ok(teacher);
    }
}