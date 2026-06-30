# Changelog

All notable changes to EmployeeHub are documented in this file.

This project follows semantic versioning.

## [1.0.0] - 2026-06-30

### Added

- Spring Boot backend with Java 21 and Maven Wrapper.
- PostgreSQL local development setup with Docker Compose.
- Department CRUD APIs.
- Employee CRUD APIs.
- Employee termination workflow using `TERMINATED` status instead of hard delete.
- Dashboard summary API with employee counts, department counts, and recent hires.
- DTO-based REST contracts.
- Bean Validation request validation.
- Global exception handling.
- Swagger/OpenAPI documentation.
- Seed data for local development.
- React, TypeScript, Vite, Axios, and TailwindCSS frontend.
- Dashboard, Employees, Departments, and create/edit management views.
- Backend service tests and dashboard controller MockMvc test.
- Windows setup documentation.

### Verified

- Backend build and tests.
- Frontend production build.
- PostgreSQL startup through Docker Compose.
- Seed data loading.
- Swagger UI availability.

### Known Limitations

- No authentication or authorization.
- No database migration tool.
- No CI/CD pipeline.
- No frontend automated tests.
