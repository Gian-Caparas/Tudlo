# Tudlo: Learning Analytics for Tracking Progress and Identifying Learning Gaps

Tudlo is a web-based learning analytics system that helps teachers see **where** students struggle, not just how they scored overall. It combines LMS assessment results and submission records into one dashboard, analyzes performance by topic and learning outcome, tracks progress over time, and flags students who may need additional support.

> **Course:** CSIT321 – Applications Development and Emerging Technology | CSIT340 - Industry Elective

> **School:** Cebu Institute of Technology – University, College of Computer Studies

> **Team:** Groufings

---

## Table of Contents

1. [The Problem](#the-problem)
2. [Features](#features)
3. [Tech Stack](#tech-stack)
4. [Project Structure](#project-structure)
5. [Getting Started](#getting-started)
6. [Configuration](#configuration)
7. [CSV Import Template](#csv-import-template)
8. [Business Rules](#business-rules)
9. [API Overview](#api-overview)
10. [Git Workflow](#git-workflow)
11. [Testing](#testing)
12. [Sprint Plan](#sprint-plan)
13. [Project Objectives](#project-objectives)
14. [Team](#team)

---

## The Problem

LMS records mostly show overall grades and separate activity reports. A student may perform well in design but struggle with programming, yet the overall grade hides this difference. Reviewing scores, submissions, and topic performance one by one is slow, which delays support for students who are falling behind.

## Features

### Must-have
- **Teacher accounts and class management**: secure login, manage assigned classes and student records
- **Data entry and CSV import**: upload assessment and submission records, with row validation; missing scores are distinguished from zero scores
- **Topic and outcome mapping**: assign questions or rubric criteria to topics, learning outcomes, and maximum scores
- **Analytics dashboard**: class averages, pass rates, submission completion, individual performance
- **Learning-gap analysis**: topic performance heatmap and most-missed questions or criteria
- **Progress tracking and support flags**: performance across assessments; flags for below-threshold scores and overdue submissions

### Should-have
- Report filters (class, student, assessment, topic, learning outcome)
- Support activity records with follow-up assessment comparison
- Report export (CSV; PDF as a stretch goal)

### Nice-to-have (out of scope for the 2-week sprint)
- AI-assisted insights
- Student access to their own progress
- Direct LMS API integration

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React 19, Vite 8, Tailwind CSS 4 |
| Backend | Spring Boot 4.1.2, Java 21, Spring Web MVC, Spring Data JPA, Spring Security, Bean Validation, JJWT 0.12.6 |
| Database | MariaDB (XAMPP for local development) |
| Test database | H2 |

## Project Structure

```text
Tudlo/
├─ README.md
├─ backend/
│  └─ tudlo/                         Spring Boot API
│     ├─ pom.xml
│     └─ src/
│        ├─ main/java/com/csit321G6/groufings/tudlo/
│        │  ├─ TudloApplication.java
│        │  ├─ config/               application and security configuration
│        │  ├─ common/               shared CRUD base classes and API errors
│        │  ├─ security/             role and data-ownership policies
│        │  ├─ controller/           REST endpoints
│        │  ├─ dto/                  request and response data transfer objects
│        │  ├─ entity/               JPA entities and domain enums
│        │  ├─ repo/                 Spring Data repositories
│        │  └─ service/              business logic
│        ├─ main/resources/
│        │  ├─ application.properties
│        │  ├─ application-local.properties.example
│        │  └─ db/seed.sql
│        └─ test/java/com/csit321G6/groufings/tudlo/
│           └─ TudloApplicationTests.java
└─ frontend/
   └─ tudlo/                          React app
      ├─ package.json
      ├─ vite.config.js
      ├─ public/
      └─ src/
         ├─ App.jsx
         ├─ main.jsx
         ├─ components/
         ├─ features/
         ├─ layout/
         ├─ mockdata/
         ├─ pages/
         │  ├─ admin/
         │  ├─ student/
         │  ├─ teacher/
         │  └─ Login.jsx
         └─ services/
```

The backend uses a **layer-based package structure**: controllers handle HTTP requests, services contain business logic, repositories access persisted data, entities represent the domain, and DTOs define request and response data. Shared classes and configuration remain in `common/` and `config/`.

The frontend keeps screen-level page components under `pages/`, grouped by role (`admin`, `teacher`, and `student`). Feature-specific modules and authentication components live under `features/`.

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+ (or use the included `mvnw`)
- Node.js 20+ and npm
- MariaDB 10.4+ (for example, the MariaDB server included with XAMPP)

### 1. Clone the repository

```bash
git clone https://github.com/Gian-Caparas/Tudlo.git
cd Tudlo
```

### 2. Set up the database

Start the MySQL module in XAMPP, then create a database named `tudlo` in phpMyAdmin.
For local development, Hibernate creates or updates the tables when the backend starts. On a fresh database, start the backend once to create the schema before loading the demo rows.

### 3. Configure and run the backend

Set a local JWT secret of at least 32 bytes and the first Developer credentials in the current PowerShell session:

```powershell
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$bytes = New-Object byte[] 48
$rng.GetBytes($bytes)
$env:APP_JWT_SECRET = [Convert]::ToBase64String($bytes)
$rng.Dispose()
$env:APP_BOOTSTRAP_DEVELOPER_EMAIL = 'developer@tudlo.test'
$env:APP_BOOTSTRAP_DEVELOPER_PASSWORD = 'replace-with-a-local-password'
```

Then start the API:

```powershell
cd backend\tudlo
.\mvnw.cmd spring-boot:run
```

- API: http://localhost:8080

On startup, Tudlo creates one active Developer only when both bootstrap variables are set and no Developer exists. Its password is BCrypt-hashed. The seed does not create a Developer. Never commit your real environment values or local properties.

On a fresh database, stop the backend after its first successful startup, then load the demo users and profile rows (re-running is safe):

```powershell
cd backend\tudlo
Get-Content -Raw .\src\main\resources\db\seed.sql | mariadb --user=root tudlo
```

The seed creates `admin@tudlo.test`, `teacher@tudlo.test`, and `student1@tudlo.test`, each with password `ChangeMe123!`. Change these demo credentials before using the database outside local development. Restart the backend after loading the seed.

The checked-in [application-local.properties.example](backend/tudlo/src/main/resources/application-local.properties.example) documents local-only settings. To use it, copy it to `application-local.properties` in the same resources folder, set the referenced environment variables, and activate the `local` profile (for example, set `$env:SPRING_PROFILES_ACTIVE = 'local'` before launching). The real file is ignored by Git.

### 4. Run the frontend

```bash
cd frontend\tudlo
npm install
npm run lint
npm run dev
```

- App: http://localhost:5173

## Configuration

### Backend

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/tudlo
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
app.jwt.secret=${APP_JWT_SECRET:}
app.jwt.expiration-minutes=${APP_JWT_EXPIRATION_MINUTES:480}
app.bootstrap.developer-email=${APP_BOOTSTRAP_DEVELOPER_EMAIL:}
app.bootstrap.developer-password=${APP_BOOTSTRAP_DEVELOPER_PASSWORD:}
```

The backend refuses to start without `APP_JWT_SECRET` containing at least 32 bytes. `APP_BOOTSTRAP_DEVELOPER_EMAIL` and `APP_BOOTSTRAP_DEVELOPER_PASSWORD` must both be set to create the first Developer. MariaDB credentials can be supplied through `DB_USERNAME` and `DB_PASSWORD`.

### Frontend

```env
VITE_API_URL=http://localhost:8080/api
```

Copy `frontend/tudlo/.env.example` to `.env` to override the API URL. The frontend stores the complete login session in `localStorage` under `tudlo_session` so refresh preserves login. This makes the bearer token readable to JavaScript; production deployments should consider secure, HttpOnly cookies to reduce token exposure if an XSS vulnerability occurs.

Never commit real passwords or JWT secrets.

## CSV Import Template

Tudlo uses a **long format** (one row per student per item) because it is easy to validate:

```csv
student_no,item_label,points_earned,submitted_at
2023-0001,Q1,4,2026-11-10
2023-0001,Q2,,2026-11-10
2023-0002,Q1,0,2026-11-10
```

- A **blank** `points_earned` means the score is **missing**.
- A `0` means the student **scored zero**.
- Download the template from the Import page or `GET /api/imports/template`.

Each row is validated for: unknown `student_no`, unknown `item_label`, points above the item's maximum, negative values, wrong data types, and duplicate rows. The import returns a summary:

```json
{
  "totalRows": 120,
  "imported": 117,
  "errors": [
    { "row": 14, "message": "Unknown student_no 2023-9999" }
  ]
}
```

Valid rows are imported and invalid rows are reported, so one bad row never blocks the whole file.

## Business Rules

### Roles and access

The server enforces strict role ranking: **DEVELOPER (4) > ADMIN (3) > TEACHER (2) > STUDENT (1)**. A manager can act only on a strictly lower-ranked account. API-created DEVELOPER accounts and promotion to DEVELOPER are forbidden; the first Developer is provisioned only by the startup bootstrap or an out-of-band database operation. Users cannot change or deactivate their own account or delete themselves. The system blocks actions that would remove the last active ADMIN or DEVELOPER.

| Area | DEVELOPER | ADMIN | TEACHER | STUDENT |
|---|---|---|---|---|
| Users, Admin, Teacher profiles | Manage strictly lower roles; cannot create or manage Developers | Manage strictly lower roles | No access | No access |
| Student profiles | Full access subject to hierarchy | Full access subject to hierarchy | GET only | No access |
| System maintenance | Full access | Full access | No access | No access |
| Academic endpoints | Full access | No access | CRUD with ownership checks on classes, submissions, and item scores | GET own submissions and item scores |

Teachers' ownership checks are implemented for classes, submissions, and item scores. Ownership enforcement on enrollments, topics, learning outcomes, assessments, assessment items, and support activities remains incomplete; authenticated teachers currently have the endpoint-level CRUD permission for those resources. Student scoping is enforced for submissions and item scores.

Account creation records the actor in `user.created_by`. Account creation, update, and deletion write structured audit log entries without password data. Profile lookup uses the existing role-specific profile tables; profile IDs are returned when the corresponding profile row exists (the Developer profile ID is always null).

The current JPA mapping stores `user_type` as a string column and declares no Hibernate `CHECK` constraint. No SQL change is required for the standard schema. If an existing database has a manually added role constraint, inspect it with `SHOW CREATE TABLE \`user\`;` and remove only that custom constraint before inserting `DEVELOPER`.

| Rule | Definition |
|---|---|
| Missing vs zero | `points_earned` is nullable, with an `is_missing` flag |
| Item percentage | `points_earned / max_points` |
| Topic performance (per student) | `SUM(earned) / SUM(max)` over the topic's items; missing scores are excluded and shown as "no data" |
| Heatmap | Rows are students, columns are topics, cells are colored red to green by topic percentage |
| Most-missed items | `1 − SUM(earned) / SUM(max)` per item across the class, sorted descending |
| Pass rate | Share of students at or above the passing percentage (default 75%, configurable) |
| Support flag | Student score below the teacher's threshold, or a submission past its due date |
| Authorization | The API applies role permissions and the ownership scopes described above |

## API Overview

All endpoints are prefixed with `/api`. Swagger/OpenAPI is not configured in the current `pom.xml`, so there is no Swagger UI endpoint.

| Feature | Endpoints |
|---|---|
| Authentication | `POST /auth/login`, authenticated `GET /auth/me` |
| Account and profile management | `/users`, `/admins`, `/teachers`, `/students` |
| System maintenance | `/system-maintenance` |
| Academic resources | `/classes`, `/enrollments`, `/topics`, `/learning-outcomes`, `/assessments`, `/assessment-items`, `/submissions`, `/item-scores`, `/support-activities` |

Login accepts `{"email":"...","password":"..."}` (the legacy `username` alias is also accepted). The response contains a `Bearer` token, `expiresInSeconds`, user identity and role, status, creation time, and `profileId`. Send protected requests with `Authorization: Bearer <token>`. Invalid login credentials all return `401` with the same generic message.

## Git Workflow

- **Branches:** `main` (stable), `development` (integration), `feature/<module>-<name>` (work)
- Every merge into `development` goes through a **pull request reviewed by another member**.
- **Commit messages:** short and imperative, for example `Add CSV validation for max points`.
- Merge `development` into `main` only at the integration checkpoints and at the final demo.

**Definition of done:** the feature works end-to-end, has no console or server errors, matches the Figma screen, and is merged into `development`.

## Testing

```bash
# Backend
cd backend\tudlo
.\mvnw.cmd test

# Frontend
cd frontend\tudlo
npm run lint
npm run build
```

- Backend tests use JUnit 5, Mockito, and H2. The H2 URL enables `NON_KEYWORDS=USER`, so the existing `user` table name is retained.
- No frontend test script or component-test framework is currently defined in `package.json`; lint and production build are the configured frontend checks.
- `db/seed.sql` includes only the demo Administrator, Teacher, and Student account/profile rows.

### Manual full-stack login check

1. Start MariaDB, create the `tudlo` database, and initialize its tables/load `db/seed.sql` as described above.
2. In one terminal, set `APP_JWT_SECRET` and start the backend with `.\mvnw.cmd spring-boot:run` from `backend\tudlo`.
3. In a second terminal, run `npm run dev` from `frontend\tudlo`.
4. Open http://localhost:5173 and sign in with `admin@tudlo.test` / `ChangeMe123!`.
5. In browser DevTools Network, confirm `POST http://localhost:8080/api/auth/login` and the subsequent authenticated `GET http://localhost:8080/api/auth/me` both return `200`. Refresh the page to confirm the session is restored, then log out and confirm the login page returns.
6. Try an incorrect password and confirm login is rejected with a generic error. The seeded Teacher and Student credentials use the same local-only demo password.

To check the API directly from PowerShell, while the backend is running:

```powershell
$login = Invoke-RestMethod -Method Post `
  -Uri 'http://localhost:8080/api/auth/login' `
  -ContentType 'application/json' `
  -Body (@{ email = 'admin@tudlo.test'; password = 'ChangeMe123!' } | ConvertTo-Json)

Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/me' `
  -Headers @{ Authorization = "Bearer $($login.token)" }
```

## Sprint Plan

**Sprint:** Mon Nov 9 to Fri Nov 20, 2026 (10 working days)

| Days | Focus |
|---|---|
| D1 | Scope, schema, API contract, Figma review |
| D2–D3 | Skeletons, auth, entities, class and student management |
| D4–D5 | Topic and outcome mapping, CSV import, **Checkpoint 1** (Fri Nov 13) |
| D6–D8 | Dashboard, heatmap, most-missed, student history, flags, **Checkpoint 2** (Wed Nov 18) |
| D9 | Support activities, filters, CSV export, integration fixes |
| D10 | Final QA, documentation, demo |

## Project Objectives

1. Import at least 95% of valid CSV records correctly and flag invalid rows with error messages.
2. Categorize 100% of imported assessment items by topic, learning outcome, and maximum score.
3. Generate each dashboard report in 5 seconds or less for a class of up to 50 students.
4. Flag students by teacher-defined thresholds and let teachers record support activities and compare follow-up results.
5. Reach an average usability rating of at least 4.0 out of 5.0 from at least 5 teacher respondents.

## Team

| Member | Role |
|---|---|
| Gian Joebert B. Caparas | Fullstack developer and team lead |
| Melody Ness B. Ecarma | Frontend developer |
| Laurence Andrey M. Baraga  | Backend developer |

---

*Tudlo is a school project for CSIT321.*
