package com.mergingtonhigh.schoolmanagement.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class SchoolManagementSystemIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldStartApplicationSuccessfully() {
        assertNotNull(restTemplate);
        assertNotNull(mongoDBContainer);
        assert(mongoDBContainer.isRunning());
    }

    @Test
    void shouldReturnAvailableDaysFromActivitiesEndpoint() {
        String url = "http://localhost:" + port + "/activities/days";
        
        ResponseEntity<List<String>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // The response might be empty initially, but the endpoint should work
    }

    @Test
    void shouldReturnActivitiesFromActivitiesEndpoint() {
        String url = "http://localhost:" + port + "/activities";
        
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Response should be a JSON object (empty map if no activities)
        assert(response.getBody().startsWith("{"));
        assert(response.getBody().endsWith("}"));
    }

    @Test
    void shouldReturnHealthStatusFromActuator() {
        String url = "http://localhost:" + port + "/actuator/health";
        
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assert(response.getBody().contains("\"status\":\"UP\""));
    }

    @Test
    void shouldServeStaticContentFromRoot() {
        String url = "http://localhost:" + port + "/";
        
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Should return HTML content from static resources
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}