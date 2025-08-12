package com.mergingtonhigh.schoolmanagement.application.dtos;

public record LoginRequestDTO(
    String username,
    String password
) {}