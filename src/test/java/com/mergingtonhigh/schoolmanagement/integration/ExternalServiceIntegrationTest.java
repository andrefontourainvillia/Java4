package com.mergingtonhigh.schoolmanagement.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

class ExternalServiceIntegrationTest {

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void shouldMockExternalNotificationService() throws IOException, InterruptedException {
        // Setup mock for external notification service
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/api/notifications"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Notification sent successfully\", \"id\": \"12345\"}")));

        // Test HTTP client calling the mocked service
        HttpClient client = HttpClient.newHttpClient();
        String requestBody = "{\"to\": \"student@example.com\", \"message\": \"You are registered for Chess Club\"}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8089/api/notifications"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNotNull(response.body());
        assert(response.body().contains("Notification sent successfully"));
    }

    @Test
    void shouldMockExternalAuthenticationService() throws IOException, InterruptedException {
        // Setup mock for external authentication service
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/api/auth/validate?token=valid-token"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"valid\": true, \"username\": \"teacher1\", \"role\": \"TEACHER\"}")));

        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/api/auth/validate?token=invalid-token"))
                .willReturn(WireMock.aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"valid\": false, \"error\": \"Invalid token\"}")));

        // Test valid token
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8089/api/auth/validate?token=valid-token"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assert(response.body().contains("\"valid\": true"));
        assert(response.body().contains("teacher1"));

        // Test invalid token
        HttpRequest invalidRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8089/api/auth/validate?token=invalid-token"))
                .GET()
                .build();

        HttpResponse<String> invalidResponse = client.send(invalidRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, invalidResponse.statusCode());
        assert(invalidResponse.body().contains("\"valid\": false"));
    }

    @Test
    void shouldMockStudentRegistrationVerificationService() throws IOException, InterruptedException {
        // Setup mock for student verification service
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/api/students/verify"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"verified\": true, \"studentId\": \"ST001\", \"name\": \"John Doe\"}")));

        // Test student verification
        HttpClient client = HttpClient.newHttpClient();
        String requestBody = "{\"email\": \"student@mergington.edu\", \"activityCode\": \"CHESS001\"}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8089/api/students/verify"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assert(response.body().contains("\"verified\": true"));
        assert(response.body().contains("John Doe"));
    }
}