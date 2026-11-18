# Clínica · API de citas

[![CI](https://github.com/aaroonpereez/clinica-citas-api/actions/workflows/ci.yml/badge.svg)](https://github.com/aaroonpereez/clinica-citas-api/actions/workflows/ci.yml)

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
├── config/            OpenApiConfig, ClockConfig
├── common/exception/  Excepciones y GlobalExceptionHandler (respuestas ProblemDetail)
├── paciente/          CRUD de pacientes
├── medico/            CRUD de médicos (baja lógica)
└── cita/              Reserva, estados, agenda e historial
```

## Endpoints

### Pacientes
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/pacientes?page=0&size=20` | Listado paginado |
| GET | `/api/v1/pacientes/{id}` | Detalle |
| POST | `/api/v1/pacientes` | Alta (DNI y email únicos) |
| PUT | `/api/v1/pacientes/{id}` | Actualizar (el DNI no cambia) |
| DELETE | `/api/v1/pacientes/{id}` | Borrar (409 si tiene citas) |
| GET | `/api/v1/pacientes/{id}/citas` | Historial de citas |

### Médicos
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/medicos?especialidad=CARDIOLOGIA` | Listado paginado, filtro opcional |
| GET | `/api/v1/medicos/{id}` | Detalle |
| POST | `/api/v1/medicos` | Alta (nº de colegiado único) |
| PUT | `/api/v1/medicos/{id}` | Actualizar |
| DELETE | `/api/v1/medicos/{id}` | Baja lógica (no admite nuevas citas) |
| PATCH | `/api/v1/medicos/{id}/activar` | Reactivar |
| GET | `/api/v1/medicos/{id}/agenda?fecha=2026-12-15` | Agenda del día |

### Citas
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/citas` | Reservar |
| GET | `/api/v1/citas/{id}` | Detalle |
| PATCH | `/api/v1/citas/{id}/confirmar` | PROGRAMADA → CONFIRMADA |
| PATCH | `/api/v1/citas/{id}/cancelar` | Solo antes de que empiece |
| PATCH | `/api/v1/citas/{id}/completar` | Solo una vez empezada |
| PATCH | `/api/v1/citas/{id}/no-presentado` | Solo una vez empezada |

## Reglas de negocio de las citas

- La hora de fin debe ser posterior a la de inicio, y la cita debe ser futura (400).
- Un médico dado de baja no admite citas (409).
- No puede solaparse con otra cita no cancelada **del médico** ni **del paciente** (409). Citas contiguas (10:00-10:30 y 10:30-11:00) sí se permiten.
- Al reservar se bloquea la fila del médico (`SELECT ... FOR UPDATE`), así dos reservas simultáneas no pueden ocupar el mismo hueco.
- Las transiciones de estado están en la propia entidad `Cita`, de modo que no se pueden saltar desde fuera.

## Hoja de ruta

- [x] Estructura, Docker, CRUD de pacientes, manejo de errores, Swagger
- [x] CRUD de médicos
- [x] Citas: reserva sin solapes, estados, agenda e historial
- [x] Integración continua con GitHub Actions
- [ ] Spring Security + JWT (roles ADMIN, MEDICO, PACIENTE)
- [ ] Semana 3-4: más tests (`@WebMvcTest`, `@DataJpaTest`, Testcontainers), Flyway para migraciones, GitHub Actions
