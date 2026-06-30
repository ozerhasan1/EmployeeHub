# EmployeeHub

Spring Boot backend for the EmployeeHub workforce management application.

## Requirements

- Java 21
- Docker or Docker Desktop
- Maven is provided by the included Maven Wrapper

## Run Backend Locally

```bash
docker compose up -d
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
docker compose up -d
.\mvnw.cmd spring-boot:run
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

API base URL:

```text
http://localhost:8080/api/v1
```

## Useful Endpoints

```text
GET    /api/v1/departments
POST   /api/v1/departments
GET    /api/v1/departments/{id}
PUT    /api/v1/departments/{id}
DELETE /api/v1/departments/{id}

GET    /api/v1/employees
POST   /api/v1/employees
GET    /api/v1/employees/{id}
PUT    /api/v1/employees/{id}
PATCH  /api/v1/employees/{id}/terminate
```

Employees are terminated through the API instead of hard-deleted.

## Configuration

The application reads local defaults from `src/main/resources/application.yml`.
Environment variables can override the database connection:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
APP_SEED_ENABLED
```

The local Docker PostgreSQL is exposed on host port `5433` by default to avoid conflicts with a PostgreSQL service already running on Windows port `5432`.

For detailed Windows setup, see [WINDOWS_SETUP.md](WINDOWS_SETUP.md).

## Run Frontend Locally

The frontend lives in `frontend/`.

```powershell
cd frontend
npm install
npm run dev
```

Frontend URL:

```text
http://127.0.0.1:5173
```
