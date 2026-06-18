# Tech Stack

## Core

- **Language**: Java 17
- **Framework**: Spring Boot 3.4.1
- **Build**: Maven (pom.xml)
- **Database**: H2 in-memory (dev), JPA/Hibernate for persistence
- **Frontend**: Static HTML/CSS/JS served from `src/main/resources/static/`

## Dependencies

- `spring-boot-starter-web` — REST API
- `spring-boot-starter-data-jpa` — Data access layer
- `h2` — In-memory database (runtime scope)
- `spring-boot-starter-test` — Testing (JUnit 5, MockMvc, etc.)
- `jqwik 1.9.2` — Property-based testing
- `jqwik-spring 0.12.0` — Spring integration for jqwik

## Common Commands

```bash
# Build the project
mvn clean package

# Run tests (includes *Test.java, *Tests.java, *Properties.java)
mvn test

# Run the application (port 8080)
mvn spring-boot:run

# H2 console available at http://localhost:8080/h2-console
```

## Configuration

- Application config: `src/main/resources/application.properties`
- Server port: 8080
- DB URL: `jdbc:h2:mem:herocardsdb`
- DDL strategy: `update` (Hibernate auto-generates schema)

## Testing Strategy

- **Property-based tests** (jqwik): Located in `src/test/java/com/javahero/property/` with `*Properties.java` suffix
- **Integration tests**: Located in `src/test/java/com/javahero/controller/` with `*IntegrationTest.java` suffix
- **Unit tests**: Located in `src/test/java/com/javahero/service/` with `*Test.java` suffix
- Maven Surefire includes `**/*Properties.java` alongside standard test patterns
