# Testing Environment Setup for GitHub Codespaces

This document provides guidance for setting up and running tests in GitHub Codespaces, including WireMock integration tests.

## Test Environment Configuration

### Prerequisites

- Java 21 (configured in `.devcontainer/devcontainer.json`)
- Maven 3.9+ (included in devcontainer)
- MongoDB (via Testcontainers for integration tests)

### Environment Variables

The following environment variables are configured in `.devcontainer/devcontainer.json`:

```json
"containerEnv": {
  "SECURITY_ADMIN_USERNAME": "admin",
  "SECURITY_ADMIN_PASSWORD": "admin123",
  "TEACHER_MARIA_PASSWORD": "123123",
  "TEACHER_JOSE_PASSWORD": "123123",
  "DIRECTOR_PAULO_PASSWORD": "123123"
}
```

## Running Tests

### All Tests
```bash
mvn test
```

### Unit Tests Only
```bash
mvn test -Dtest='!**/*IntegrationTest'
```

### Integration Tests Only
```bash
mvn test -Dtest='**/*IntegrationTest'
```

### Test Coverage Report
```bash
mvn jacoco:report
# View report at target/site/jacoco/index.html
```

## Test Categories

### 1. Unit Tests
- **Domain Entities**: `TeacherTest`, `ActivityTest`
- **Application Use Cases**: `AuthenticationUseCaseTest`, `ActivityUseCaseTest`, `TeacherUseCaseTest`, `StudentRegistrationUseCaseTest`
- **Architecture Tests**: `LayerArchitectureTest`

### 2. Integration Tests
- **WireMock External Services**: `ExternalServiceIntegrationTest`
  - Mock notification services
  - Mock authentication services  
  - Mock student verification services

## WireMock Configuration

WireMock is configured for Codespaces compatibility:

```xml
<dependency>
    <groupId>org.wiremock</groupId>
    <artifactId>wiremock-standalone</artifactId>
    <version>3.9.1</version>
    <scope>test</scope>
</dependency>
```

### Usage Example

```java
@BeforeEach
void setUp() {
    wireMockServer = new WireMockServer(8089);
    wireMockServer.start();
    WireMock.configureFor("localhost", 8089);
}

@Test
void shouldMockExternalService() {
    wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/api/notifications"))
            .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"message\": \"Success\"}")));
    
    // Test HTTP client calls to localhost:8089
}
```

## Codespaces-Specific Configuration

### Port Configuration
- Application: Port 8080 (forwarded automatically)
- WireMock tests: Port 8089 (internal testing only)

### Memory Settings
JVM settings in `.devcontainer/devcontainer.json`:

```json
"settings": {
  "java.jdt.ls.vmargs": "-Xmx2G -Xms512m",
  "java.test.config": {
    "vmargs": ["-ea", "-Xmx1G"]
  }
}
```

## Test Coverage Metrics

Current coverage (as of latest run):
- **Overall Coverage**: 78%
- **Application Use Cases**: 96%
- **Domain Entities**: 78%
- **Infrastructure Config**: 100%

### Coverage Goals
- Maintain > 70% overall coverage
- Domain layer > 80%
- Application layer > 90%

## Troubleshooting

### Common Issues

1. **WireMock Jetty Conflicts**
   - Use `wiremock-standalone` version 3.9.1+
   - Avoid JUnit5 extension if incompatible

2. **MongoDB Connection Issues**
   - Testcontainers handles MongoDB for integration tests
   - No manual MongoDB setup needed in Codespaces

3. **Port Conflicts**
   - WireMock uses port 8089 by default
   - Change port if conflicts occur: `new WireMockServer(9089)`

### Debug Commands

```bash
# Check Java version
java -version

# Check Maven version  
mvn -version

# Run with debug output
mvn test -X

# Run specific test class
mvn test -Dtest=TeacherTest

# Skip tests during build
mvn package -DskipTests
```

## CI/CD Integration

The test configuration is compatible with GitHub Actions:

```yaml
- name: Run tests
  run: mvn test

- name: Generate test report
  run: mvn jacoco:report
```

All tests are designed to run in containerized environments without external dependencies.