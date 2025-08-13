# Mergington High School Management System - GitHub Copilot Instructions

**ALWAYS follow these instructions first and fallback to search or bash commands ONLY when information here is incomplete or found to be in error.**

## Entendimento do arquivo

> O check abaixo será utilizado no exercicio 3 deste repositório, o agente não deve marcar automaticamente e caso seja regerado o arquivo deverá manter a marcação anterior

[ ] - Aprendi sobre como configurar o copilot-instructions.

## Working Effectively

**Bootstrap, build, and test the repository:**

1. **Check Java version** (MUST be Java 21):
   ```bash
   java -version
   mvn -version
   ```

2. **Start MongoDB** (REQUIRED before any application operations):
   ```bash
   docker run -d -p 27017:27017 --name mongodb mongo:7.0
   ```

3. **Build the project** - NEVER CANCEL, set timeout to 90+ minutes:
   ```bash
   mvn clean install
   ```
   - **Expected time**: 37 seconds for full build with tests
   - **NEVER CANCEL**: Build may download dependencies on first run

4. **Run tests only** - NEVER CANCEL, set timeout to 30+ minutes:
   ```bash
   mvn test
   ```
   - **Expected time**: 11 seconds
   - **Tests run**: 20 tests across domain, application, and architecture layers

5. **Quick compile without tests**:
   ```bash
   mvn clean compile -DskipTests
   ```
   - **Expected time**: 4 seconds

6. **Package without tests**:
   ```bash
   mvn package -DskipTests
   ```
   - **Expected time**: 2 seconds

## Running the Application

**ALWAYS run the bootstrapping steps first.**

1. **Start the application**:
   ```bash
   SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
   ```
   - **Expected startup time**: 3 seconds
   - **Port**: 8080
   - **Profile**: Use `dev` profile for CORS configuration

2. **Access points**:
   - **Frontend**: http://localhost:8080
   - **API**: http://localhost:8080/activities
   - **Health check**: http://localhost:8080/actuator/health

## Validation

**ALWAYS manually validate any new code through complete end-to-end scenarios after making changes.**

### Required Validation Scenarios

1. **API Functionality Test**:
   ```bash
   curl -s http://localhost:8080/activities | jq 'keys | length'
   ```
   - **Expected result**: Returns count of 6 activities

2. **Filter by day test**:
   ```bash
   curl -s "http://localhost:8080/activities?day=Monday" | jq 'keys | length'
   ```
   - **Expected result**: Returns count of 2 Monday activities

3. **Student registration test** (MUST use correct teacher credentials):
   ```bash
   curl -s -X POST http://localhost:8080/activities/"Clube%20de%20Xadrez"/signup \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "email=newstudent@mergington.edu&teacher_username=maria"
   ```
   - **Expected result**: `{"message":"Inscreveu newstudent@mergington.edu em Clube de Xadrez"}`

4. **Student unregistration test**:
   ```bash
   curl -s -X POST http://localhost:8080/activities/"Clube%20de%20Xadrez"/unregister \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "email=newstudent@mergington.edu&teacher_username=maria"
   ```
   - **Expected result**: `{"message":"Desinscreveu newstudent@mergington.edu de Clube de Xadrez"}`

5. **Frontend validation**:
   ```bash
   curl -s http://localhost:8080/ | grep -c "Colégio Mergington"
   ```
   - **Expected result**: Returns count of 3 occurrences

### Coverage and Quality Checks

**Always run coverage report after testing**:
```bash
mvn jacoco:report
```
- **Coverage report location**: `target/site/jacoco/index.html`

**Run specific test classes**:
```bash
mvn test -Dtest=ActivityTest
mvn test -Dtest=StudentRegistrationUseCaseTest
```

## Architecture Overview

This is a **Clean Architecture** Spring Boot application (38 Java files total) with strict layer separation:

- **Domain Layer** (`src/main/java/.../domain/`): Pure business logic, entities, value objects, repository interfaces
- **Application Layer** (`src/main/java/.../application/`): Use cases and DTOs
- **Infrastructure Layer** (`src/main/java/.../infrastructure/`): Database, configurations, migrations
- **Presentation Layer** (`src/main/java/.../presentation/`): REST controllers and mappers

**CRITICAL**: Dependencies flow inward - domain has no outward dependencies.

## Key Configuration Data

### Teacher Credentials (for testing)
**Default teachers** (usernames for API testing):
- `maria` (Maria Rodriguez, TEACHER role, password: 123123)
- `jose` (Prof. Jose Chen, TEACHER role, password: 123123)
- `paulo` (Paulo Silva, ADMIN role, password: 123123)

### Activities Available
**Pre-seeded activities** (via Mongock migrations):
- Clube de Xadrez (Tuesday, Thursday 15:30-17:00)
- Clube de Programação (Monday, Wednesday, Friday 14:00-15:30)
- Clube de Arte (Tuesday, Thursday 16:00-17:30)
- Time de Futebol (Monday, Wednesday, Friday 16:00-18:00)
- Banda de Música (Tuesday, Thursday 15:00-16:30)
- Serviço Comunitário (Saturday 09:00-12:00)

### Development Patterns

**Entity Validation Pattern** - All domain entities validate in constructors:
```java
private String validateName(String name) {
    if (name == null || name.trim().isEmpty()) {
        throw new IllegalArgumentException("Name cannot be null or empty");
    }
    return name.trim();
}
```

**Repository Pattern** - Interfaces in `domain/repositories/`, implementations in `infrastructure/persistence/`

**Use Case Pattern** - Always validate teacher authentication first, use domain repositories through interfaces

## Common Tasks Reference

### Repository Structure
```
src/main/java/com/mergingtonhigh/schoolmanagement/
├── domain/entities/        # Activity.java, Teacher.java
├── domain/repositories/    # Repository interfaces
├── domain/valueobjects/    # Email.java, ScheduleDetails.java
├── application/usecases/   # Business logic coordination
├── application/dtos/       # Data transfer objects
├── infrastructure/        # MongoDB, migrations, config
└── presentation/          # REST controllers
```

### Static Resources
- **Location**: `src/main/resources/static/`
- **Files**: `index.html`, `app.js`, `styles.css`
- **Frontend**: Vanilla JavaScript with responsive design

### Build Artifacts
- **JAR location**: `target/school-management-system-0.0.1-SNAPSHOT.jar`
- **Coverage**: `target/site/jacoco/index.html`
- **Executable JAR**: Includes all dependencies (Spring Boot fat JAR)

### Environment Variables
**Optional overrides** (defaults in parentheses):
- `TEACHER_MARIA_PASSWORD` (123123)
- `TEACHER_JOSE_PASSWORD` (123123)
- `DIRECTOR_PAULO_PASSWORD` (123123)
- `SPRING_PROFILES_ACTIVE` (use `dev` for development)

## Error Handling and Debugging

**HTTP Status Codes**:
- `401`: Authentication failures (invalid teacher credentials)
- `404`: Activity not found
- `400`: Validation errors (invalid email format, etc.)

**Authentication Requirements**:
- Student registration/unregistration requires valid teacher credentials
- Use form data: `email=student@email.com&teacher_username=validteacher`

**MongoDB Connection**:
- **Default**: `localhost:27017`
- **Database**: `mergington_high`
- **Auto-migration**: Mongock runs on startup

## Critical Warnings

- **NEVER CANCEL** any build or test commands - builds may take up to 45 minutes on first run
- **ALWAYS** set timeouts of 60+ minutes for build commands and 30+ minutes for test commands
- **ALWAYS** start MongoDB before running application
- **ALWAYS** use `dev` profile for development (`SPRING_PROFILES_ACTIVE=dev`)
- **ALWAYS** validate functionality with complete user scenarios after changes
