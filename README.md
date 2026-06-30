# EmployeeHub

EmployeeHub is a full-stack internal workforce management application for managing employees, departments, and HR dashboard metrics.

## Business Problem

Small companies often track employee and department information across spreadsheets, email threads, and disconnected tools. EmployeeHub provides a simple internal system where HR staff can manage core workforce records, review department staffing, and monitor high-level employee status metrics from one place.

## Architecture Overview

EmployeeHub uses a conventional layered architecture:

- React frontend calls the backend through REST APIs.
- Spring Boot controllers expose HTTP endpoints and remain thin.
- Services contain business rules and transaction boundaries.
- Repositories handle persistence through Spring Data JPA.
- DTOs isolate API contracts from JPA entities.
- PostgreSQL stores employee and department data.

## Technology Stack

Backend:

- Java 21
- Spring Boot 3
- Maven Wrapper
- Spring Web
- Spring Data JPA
- Hibernate
- Bean Validation
- Lombok
- PostgreSQL
- Springdoc OpenAPI / Swagger
- JUnit 5 and Mockito

Frontend:

- React
- TypeScript
- Vite
- Axios
- TailwindCSS

Development:

- Docker Compose for PostgreSQL
- Git

## Project Structure

```text
.
├── docker-compose.yml
├── pom.xml
├── src/
│   ├── main/java/com/hasanozer/employeehub/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── enums/
│   │   ├── exception/
│   │   ├── mapper/
│   │   ├── repository/
│   │   ├── service/
│   │   └── util/
│   └── test/java/com/hasanozer/employeehub/
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   ├── components/
│   │   ├── styles/
│   │   └── types/
│   └── package.json
└── WINDOWS_SETUP.md
```

## Features

- Employee CRUD
- Employee termination instead of hard delete
- Department CRUD
- Dashboard summary
- Employee search, pagination, sorting, and filtering
- Validation and global exception handling
- Seed data for local development
- Swagger API documentation
- Responsive React frontend

## Database Overview

The database contains two core tables:

- `departments`: department name, description, and audit timestamps
- `employees`: employee profile fields, employment status, hire date, salary, department reference, and audit timestamps

Employees belong to one department. Departments with assigned employees cannot be deleted. Employee statuses are `ACTIVE`, `ON_LEAVE`, and `TERMINATED`.

## REST API Overview

Base URL:

```text
http://localhost:8080/api/v1
```

Main endpoints:

```text
GET    /departments
POST   /departments
GET    /departments/{id}
PUT    /departments/{id}
DELETE /departments/{id}

GET    /employees
POST   /employees
GET    /employees/{id}
PUT    /employees/{id}
PATCH  /employees/{id}/terminate

GET    /dashboard/summary
```

## Swagger Documentation

Start the backend and open:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON is available at:

```text
http://localhost:8080/api-docs
```

## Installation

Required tools:

- Java 21
- Docker Desktop
- Node.js 20 or newer

Maven does not need to be installed separately because the repository includes Maven Wrapper.

For Windows-specific setup, see [WINDOWS_SETUP.md](WINDOWS_SETUP.md).

## Running Locally

Start PostgreSQL:

```powershell
docker compose up -d
```

Run backend:

```powershell
.\mvnw.cmd spring-boot:run
```

Run frontend:

```powershell
cd frontend
npm install
npm run dev
```

Frontend URL:

```text
http://127.0.0.1:5173
```

Backend URL:

```text
http://localhost:8080
```

## Docker Setup

Docker Compose starts PostgreSQL 16 Alpine.

Default local database settings:

```text
Database: employeehub
Username: employeehub
Password: employeehub
Host port: 5433
Container port: 5432
```

The Spring datasource can be overridden with environment variables:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
APP_SEED_ENABLED
```

## Testing

Backend:

```powershell
.\mvnw.cmd clean test
```

Frontend build:

```powershell
cd frontend
npm install
npm run build
```

Useful API checks:

```powershell
Invoke-RestMethod http://localhost:8080/api/v1/departments
Invoke-RestMethod http://localhost:8080/api/v1/employees
Invoke-RestMethod http://localhost:8080/api/v1/dashboard/summary
Invoke-WebRequest http://localhost:8080/swagger-ui/index.html
```

## Known Limitations

- No authentication or authorization.
- No database migration tool.
- No CI/CD pipeline.
- No frontend automated tests.
- Local development uses Hibernate `ddl-auto=update`.

## Future Improvements

Appropriate Version 2 improvements include authentication, role-based access, database migrations, frontend test coverage, CI checks, audit history, and richer reporting.

## License Recommendation

MIT is recommended for this portfolio project because it is simple, permissive, and familiar to open source reviewers.
