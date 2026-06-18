# Project Structure

```
src/main/java/com/javahero/
├── config/              # App configuration and data initialization
├── controller/          # REST API controllers (@RestController)
├── dto/                 # Data Transfer Objects (request/response payloads)
├── exception/           # Custom exception classes
├── model/               # JPA entities and enums
├── repository/          # Spring Data JPA repositories
├── service/             # Business logic layer (@Service)
└── JavaHeroCardsApplication.java

src/main/resources/
├── static/              # Frontend (HTML, CSS, JS — no framework)
│   ├── css/
│   ├── js/
│   └── *.html
└── application.properties

src/test/java/com/javahero/
├── controller/          # Integration tests (MockMvc, full context)
├── property/            # Property-based tests (jqwik)
└── service/             # Unit tests
```

## Architecture Pattern

Standard layered Spring Boot architecture:

1. **Controller** — HTTP endpoints, request validation, response mapping
2. **Service** — Business logic, orchestration, state management
3. **Repository** — Data access via Spring Data JPA interfaces
4. **Model** — JPA entities representing domain objects

## Conventions

- Controllers use constructor injection (no `@Autowired` on fields)
- Repositories extend `JpaRepository` with custom query methods
- DTOs are separate from entities — controllers never expose entities directly
- Entities use explicit getters/setters (no Lombok)
- Enums stored as `VARCHAR` in DB (`@Enumerated(EnumType.STRING)`)
- Quiz session state held in-memory via `ConcurrentHashMap` (not persisted)
- Property-based test classes use `*Properties` suffix and reference requirement IDs in Javadoc
