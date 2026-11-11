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
| Frontend | React (Vite), React Router, Axios, TanStack Query, Recharts |
| Backend | Spring Boot 4 (Java 25), Spring Web, Spring Data JPA, Spring Security (JWT), Bean Validation |
| Database | MariaDB (XAMPP for local development) |
| CSV | Apache Commons CSV |
| API Docs | springdoc-openapi (Swagger UI) |
| Tools | Git and GitHub, Postman, Figma |

## Project Structure

```text
Tudlo/
├─ README.md
├─ docker-compose.yml                (optional: local database)
├─ backend/                          Spring Boot API
│  ├─ pom.xml
│  └─ src/
│     ├─ main/java/com/tudlo/
│     │  ├─ config/        security, CORS, OpenAPI configuration
│     │  ├─ security/      JWT service and authentication filter
│     │  ├─ common/        exception handling, shared DTOs, utilities
│     │  ├─ auth/          login endpoints
│     │  ├─ user/          user entity and roles
│     │  ├─ classroom/     classes
│     │  ├─ student/       students and enrollments
│     │  ├─ curriculum/    topics and learning outcomes
│     │  ├─ assessment/    assessments, items, mapping
│     │  ├─ submission/    submissions and item scores
│     │  ├─ importer/      CSV template, validation, import
│     │  ├─ analytics/     dashboard, heatmap, most-missed, student history
│     │  ├─ support/       support activities, thresholds, flags
│     │  └─ report/        CSV export
│     ├─ main/resources/
│     │  ├─ application.yml
│     │  └─ db/migration/  V1__schema.sql, V2__seed.sql
│     └─ test/
└─ frontend/                         React app
   ├─ package.json
   ├─ vite.config.js
   └─ src/
      ├─ api/          Axios client and API modules
      ├─ auth/         AuthContext, ProtectedRoute
      ├─ components/   layout, ui, charts (heatmap, line, bar)
      ├─ pages/        Login, Classes, Mapping, Import, Dashboard,
      │                GapAnalysis, StudentProfile, SupportFlags, Reports
      ├─ hooks/
      └─ utils/        color scale, formatters
```

The backend is organized by **feature package** (not by layer) so each team member owns whole folders and merge conflicts stay low.

## Getting Started

### Prerequisites

- Java 25
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
For local development, Hibernate creates or updates the tables when the backend starts.

### 3. Run the backend

```bash
cd backend
./mvnw spring-boot:run
```

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

### 4. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

- App: http://localhost:5173

No demo users are seeded. Create a user through the API before testing dependent records.

## Configuration

### Backend: `backend/tudlo/src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/tudlo
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.flyway.enabled=false

app.jwt.secret=${JWT_SECRET:change-this-to-a-long-random-string}
app.jwt.expiration-ms=86400000
app.cors.allowed-origin=http://localhost:5173
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
```

### Frontend: `frontend/.env`

```env
VITE_API_URL=http://localhost:8080/api
```

> Never commit real passwords or JWT secrets. Use environment variables or an `application-local.yml` listed in `.gitignore`.

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

| Rule | Definition |
|---|---|
| Missing vs zero | `points_earned` is nullable, with an `is_missing` flag |
| Item percentage | `points_earned / max_points` |
| Topic performance (per student) | `SUM(earned) / SUM(max)` over the topic's items; missing scores are excluded and shown as "no data" |
| Heatmap | Rows are students, columns are topics, cells are colored red to green by topic percentage |
| Most-missed items | `1 − SUM(earned) / SUM(max)` per item across the class, sorted descending |
| Pass rate | Share of students at or above the passing percentage (default 75%, configurable) |
| Support flag | Student score below the teacher's threshold, or a submission past its due date |
| Authorization | Every query is limited to the logged-in teacher's own classes |

## API Overview

All endpoints are prefixed with `/api`. Full details are in Swagger UI.

| Feature | Endpoints |
|---|---|
| Auth | `POST /auth/login`, `GET /auth/me` |
| Classes and students | `GET/POST /classes`, `PUT/DELETE /classes/{id}`, `GET/POST /classes/{id}/students` |
| Curriculum | `GET/POST /classes/{id}/topics`, `GET/POST /classes/{id}/outcomes` |
| Assessments and mapping | `GET/POST /classes/{id}/assessments`, `GET /assessments/{id}/items`, `PUT /assessments/{id}/mapping` |
| Import | `GET /imports/template`, `POST /assessments/{id}/import`, `POST /assessments/{id}/scores` |
| Dashboard | `GET /classes/{id}/dashboard`, `GET /classes/{id}/students/{enrollmentId}/performance` |
| Gap analysis | `GET /classes/{id}/gap/heatmap`, `GET /classes/{id}/gap/most-missed` |
| Flags and support | `GET/PUT /classes/{id}/thresholds`, `GET /classes/{id}/flags`, `GET/POST /classes/{id}/support-activities` |
| Reports | `GET /classes/{id}/reports/export?type=csv` |

## Git Workflow

- **Branches:** `main` (stable), `develop` (integration), `feature/<module>-<name>` (work)
- Every merge into `develop` goes through a **pull request reviewed by another member**.
- **Commit messages:** short and imperative, for example `Add CSV validation for max points`.
- Merge `develop` into `main` only at the integration checkpoints and at the final demo.

**Definition of done:** the feature works end-to-end, has no console or server errors, matches the Figma screen, and is merged into `develop`.

## Testing

```bash
# Backend
cd backend
./mvnw test

# Frontend
cd frontend
npm run test
```

- Backend: JUnit 5, Mockito, and MockMvc for services and controllers.
- Frontend: manual test script plus a few component tests.
- Seed data: 1 teacher, 2 classes, about 30 students, 3 assessments, and 5 topics, used for development and the usability test.

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
