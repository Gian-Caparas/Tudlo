---
name: run-tudlo
description: Run and drive the Tudlo learning analytics web application (frontend + backend)
---

# Run Tudlo Learning Analytics App

Tudlo is a React + Vite frontend (port 5173) backed by a Spring Boot 4 API (port 8080) with MariaDB. This skill covers running both parts and driving them programmatically.

**Paths in this document are relative to the repo root (`D:\Web Development\Tudlo`).**

---

## Prerequisites

### Frontend
- Node.js 20+ (tested with v22.20.0)
- npm

### Backend
- Java 21 (the project uses Java 21; Java 24 on host works via Maven wrapper)
- Maven 3.9+ (wrapper `mvnw` included)
- **MariaDB/MySQL running on localhost:3306** with a database named `tudlo`
  - Or use the test profile with H2 in-memory (no external DB needed)

---

## Setup

### Frontend
```bash
cd frontend/tudlo
npm install
```

### Backend (production - requires MariaDB)
```bash
cd backend/tudlo
# Ensure MariaDB is running and `tudlo` database exists
./mvnw spring-boot:run
```

### Backend (test profile - H2 in-memory, no external DB)
```bash
cd backend/tudlo
./mvnw test
```

---

## Run (Agent Path) — Programmatic Access

### Frontend: `curl` + basic HTML check

The frontend is a standard Vite React app served at http://localhost:5173. For programmatic verification:

```bash
cd frontend/tudlo
npm run dev &
# Wait for server ready
sleep 3
curl -s http://localhost:5173 | grep -q tudlo && echo "Frontend OK"
```

For full browser automation, install Playwright:
```bash
cd frontend/tudlo
npm install -D playwright
npx playwright install chromium
# Then use npx playwright to script interactions
```

### Backend: `curl` against REST API

```bash
cd backend/tudlo
./mvnw spring-boot:run &
# Wait for server ready (check http://localhost:8080/api)
curl -s http://localhost:8080/api/classes
```

### Backend Tests (no DB required)

```bash
cd backend/tudlo
./mvnw test
# Runs with H2 in-memory database via src/test/resources/application.properties
```

---

## Run (Human Path)

### Frontend
```bash
cd frontend/tudlo
npm run dev
# Opens http://localhost:5173 in browser
```

### Backend
```bash
cd backend/tudlo
./mvnw spring-boot:run
# API: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui/index.html
```

**Note:** Backend requires MariaDB running. The human path is not usable headless without a database.

---

## Project Structure

```
Tudlo/
├─ frontend/tudlo/           # React + Vite app
│  ├─ src/
│  │  ├─ pages/              # Dashboard, ImportData, TopicMapping
│  │  └─ components/         # Sidebar, Header
│  └─ package.json
└─ backend/tudlo/            # Spring Boot 4 API
   ├─ src/main/java/...      # Controllers, services, entities, repos
   ├─ src/main/resources/
   │  └─ application.properties  # MariaDB config
   ├─ src/test/resources/
   │  └─ application.properties  # H2 in-memory config for tests
   └─ pom.xml
```

---

## Gotchas

| Issue | Workaround |
|-------|------------|
| Backend fails to start: "Connection refused" to MariaDB | Start MariaDB (XAMPP MySQL module) and create `tudlo` database, OR run tests with `./mvnw test` (uses H2) |
| Flyway 401 warnings on Maven download | Harmless; Flyway community repo requires auth but Maven Central has the artifacts |
| Frontend shows blank page | Check browser console for API errors (backend not running or CORS) |
| Java version mismatch | Project uses Java 21 in `pom.xml`; wrapper downloads correct JDK |
| `npm run dev` hangs in background | Run with `&` and `curl` to verify, or use `chromium-cli` script |

---

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| Backend: `JDBCConnectionException: Connection refused` | Start MariaDB: `net start mysql` (Windows) or ensure XAMPP MySQL is running; create `tudlo` DB in phpMyAdmin |
| Backend: `Flyway 401` warnings | Ignore — artifacts resolve from Maven Central |
| Frontend: `VITE_API_URL` not set | Create `frontend/tudlo/.env` with `VITE_API_URL=http://localhost:8080/api` |
| Tests fail with H2 syntax errors | Some MariaDB-specific DDL may not translate; tests currently only verify context loads |

---

## Quick Verification

```bash
# Frontend
curl -s http://localhost:5173 | grep -q tudlo && echo "Frontend OK"

# Backend (requires DB)
curl -s http://localhost:8080/api/classes && echo "Backend OK"

# Backend tests (always works)
cd backend/tudlo && ./mvnw test -q && echo "Tests OK"
```