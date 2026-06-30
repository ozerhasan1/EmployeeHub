# EmployeeHub Windows Setup

This project requires Java 21, Docker Desktop, and PostgreSQL through Docker Compose.
Maven is provided by the included Maven Wrapper, so a separate Maven installation is optional.

## 1. Install Java 21

Install a JDK 21 distribution, for example:

- Eclipse Temurin 21
- Microsoft Build of OpenJDK 21
- Oracle JDK 21

After installation, open a new PowerShell window and verify:

```powershell
java -version
```

Expected result: the output should mention version `21`.

If Windows still shows an older Java version, update `JAVA_HOME` and `Path`:

```powershell
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-21"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
java -version
```

For a permanent setup, update Environment Variables in Windows System Settings.

## 2. Configure IntelliJ IDEA

1. Open the project folder: `C:\Users\hasan\EmployeeHub`.
2. Go to `File > Project Structure > Project`.
3. Set `SDK` to a Java 21 JDK.
4. Set `Language level` to `21`.
5. Go to `Settings > Build, Execution, Deployment > Build Tools > Maven`.
6. Use the Maven Wrapper or bundled Maven.
7. Reimport Maven if IntelliJ asks.

## 3. Install Docker Desktop

Install Docker Desktop for Windows and start it.

Docker Desktop with the WSL 2 backend requires hardware virtualization to be enabled in BIOS/UEFI.
If `wsl --status` reports that virtualization is not enabled, reboot into firmware settings and enable Intel VT-x or AMD-V.

Verify Docker from PowerShell:

```powershell
docker --version
docker compose version
```

Docker Desktop must be running before starting PostgreSQL.

## 4. Start PostgreSQL

From the project root:

```powershell
docker compose config
docker compose up -d
docker ps
```

The database defaults are:

```text
Database: employeehub
Username: employeehub
Password: employeehub
Host port: 5433
Container port: 5432
```

## 5. Build And Test

From the project root:

```powershell
.\mvnw.cmd clean test
```

The first run downloads Maven and project dependencies.

## 6. Run Backend

```powershell
.\mvnw.cmd spring-boot:run
```

The backend starts on:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

## 7. Useful Verification Commands

```powershell
java -version
docker compose config
docker compose up -d
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

## Troubleshooting

If the project fails with `release version 21 not supported`, IntelliJ or the terminal is using a JDK older than 21.

If the backend cannot connect to PostgreSQL, verify Docker Desktop is running and the container is healthy:

```powershell
docker ps
docker logs employeehub-postgres
```

If port `5432` is already used, set a different host port:
The project already defaults to host port `5433` to avoid conflicts with local PostgreSQL installs.

```powershell
$env:POSTGRES_PORT="5434"
docker compose up -d
```

Then also override the Spring datasource URL:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5434/employeehub"
.\mvnw.cmd spring-boot:run
```
