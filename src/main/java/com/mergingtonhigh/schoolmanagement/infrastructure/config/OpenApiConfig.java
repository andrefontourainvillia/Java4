package com.mergingtonhigh.schoolmanagement.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("dev")
public class OpenApiConfig {

    @Bean
    public OpenAPI schoolManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mergington High School Management System API")
                        .description("API for managing extracurricular activities at Mergington High School")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("School Management Team")
                                .email("admin@mergingtonhigh.edu")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server"),
                        new Server()
                                .url("https://api.mergingtonhigh.edu")
                                .description("Production server")));
    }
}