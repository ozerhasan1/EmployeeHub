# EmployeeHub Frontend

React, TypeScript, Vite, Axios, and TailwindCSS frontend for EmployeeHub.

## Run Locally

Start the backend first from the repository root:

```powershell
docker compose up -d
.\mvnw.cmd spring-boot:run
```

Then start the frontend:

```powershell
cd frontend
npm install
npm run dev
```

Frontend URL:

```text
http://127.0.0.1:5173
```

The Vite dev server proxies `/api` requests to:

```text
http://localhost:8080
```
