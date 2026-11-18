# Clínica · API de citas

API REST para gestionar pacientes, médicos y citas de una clínica.

**Stack:** Java 21 · Spring Boot 4 · Spring Data JPA · PostgreSQL · Bean Validation · OpenAPI/Swagger · JUnit 5 + Mockito

## Cómo arrancarlo

Necesitas Java 21 y Docker Desktop en marcha.

```bash
docker compose up -d          # levanta PostgreSQL en localhost:5432
./mvnw spring-boot:run        # en Windows: mvnw.cmd spring-boot:run
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Tests:

```bash
./mvnw test
```

## Estructura

Organizado por funcionalidad (cada carpeta contiene su entidad, repositorio, servicio y controlador):

```
src/main/java/com/clinica/citas
├── ClinicaCitasApplication.java
├── config/            OpenApiConfig
├── common/exception/  Excepciones y GlobalExceptionHandler (respuestas ProblemDetail)
├── paciente/          CRUD completo: entidad, repo, DTOs, servicio, controlador
├── medico/            Entidad + repositorio
└── cita/              Entidad + repositorio (con consulta de solapes)
```

## Endpoints

| Método | Ruta | Estado |
|---|---|---|
| GET | `/api/v1/pacientes?page=0&size=20` | ✅ |
| GET | `/api/v1/pacientes/{id}` | ✅ |
| POST | `/api/v1/pacientes` | ✅ |
| PUT | `/api/v1/pacientes/{id}` | ✅ |
| DELETE | `/api/v1/pacientes/{id}` | ✅ |
| CRUD | `/api/v1/medicos` | ⏳ |
| POST | `/api/v1/citas` (sin solapes) | ⏳ |
| PATCH | `/api/v1/citas/{id}/cancelar` | ⏳ |
| GET | `/api/v1/medicos/{id}/agenda?fecha=` | ⏳ |

## Hoja de ruta

- [x] Semana 1: estructura, Docker, CRUD de pacientes, manejo de errores, Swagger
- [ ] Semana 1-2: CRUD de médicos (copia el patrón de `paciente/`)
- [ ] Semana 2: citas. Validar que `fin > inicio`, que la fecha sea futura y que no se solape con otra cita del médico (`CitaRepository.existeSolapeMedico`). Cancelar, completar y consultar la agenda.
- [ ] Semana 3: Spring Security + JWT (roles ADMIN, MEDICO, PACIENTE)
- [ ] Semana 3-4: más tests (`@WebMvcTest`, `@DataJpaTest`, Testcontainers), Flyway para migraciones, GitHub Actions
