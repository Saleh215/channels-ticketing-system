# Channels Ticketing System

A RESTful support ticketing backend built with Java 21 and Spring Boot for the Channels Back-End Developer Technical Assessment.

## Features

- JWT-based authentication with a 15-minute token lifetime.
- Role-based access control for **CUSTOMER**, **AGENT**, and **MONITOR**.
- Ticket creation by customers, with text validation.
- Ticket viewing, editing, and resolution by agents.
- Automatic expiry of open tickets after 30 seconds.
- Automatic removal of expired tickets 3 minutes after expiration.
- Separate login-history and ticket-status audit logs.
- Monitor-only audit endpoints.

## Tech Stack

- Java 21
- Spring Boot, Spring Web, Spring Security
- Spring Data JPA / Hibernate
- Spring Validation
- Spring Scheduling
- JWT (Spring Security OAuth2 Resource Server / Nimbus)
- MySQL 8
- Maven 3.9+

## Prerequisites

- JDK 21
- Maven 3.9+
- MySQL 8

Create the database:

```sql
CREATE DATABASE ticketing;
```

## Configuration

The application uses `src/main/resources/application.yml`. Set the following environment variables before starting it:

| Variable | Description |
| --- | --- |
| `DB_PASSWORD` | MySQL password |
| `JWT_SECRET` | A strong, random secret of at least 32 bytes for HS256 signing |
| `DB_URL` | Optional MySQL JDBC URL, if configured in `application.yml` |
| `DB_USER` | Optional MySQL username, if configured in `application.yml` |
| `SERVER_PORT` | Optional server port, if configured in `application.yml` |

The local configuration shown during development uses MySQL at `localhost:3306/ticketing`, username `root`, and port `8085`. Never commit real credentials or signing secrets. Ensure `application.yml` references environment variables rather than committed secret defaults.

## Run

From the project root (the directory containing `pom.xml`):

```bash
mvn clean package
mvn spring-boot:run
```

Base URL: `http://localhost:8085`

## API Endpoints

| Method | Endpoint | Role | Purpose |
| --- | --- | --- | --- |
| POST | `/api/auth/login` | Public | Authenticate and receive JWT |
| POST | `/api/tickets` | CUSTOMER | Create a ticket |
| GET | `/api/tickets` | AGENT | List tickets |
| GET | `/api/tickets/{id}` | AGENT | View a ticket |
| PATCH | `/api/tickets/{id}` | AGENT | Edit ticket text |
| PATCH | `/api/tickets/{id}/resolve` | AGENT | Resolve an open ticket |
| GET | `/api/audit/tickets` | MONITOR | View ticket-status logs |
| GET | `/api/audit/logins` | MONITOR | View login-history logs |

Use the JWT returned by login in subsequent protected requests:

```http
Authorization: Bearer <access-token>
```

### Login request

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "username": "<username>",
  "password": "<password>"
}
```

A successful response contains `accessToken`, `tokenType`, and `expiresIn` (900 seconds).

### Create a ticket

```http
POST /api/tickets
Authorization: Bearer <customer-token>
Content-Type: application/json
```

```json
{
  "text": "Please help me"
}
```

Ticket text must be nonblank, at most 250 characters, and contain only Unicode letters and spaces (no digits or symbols). The same validation applies to ticket edits.

### Resolve a ticket

```http
PATCH /api/tickets/{id}/resolve
Authorization: Bearer <agent-token>
```

No request body is required.

## Ticket Lifecycle

1. A customer creates a ticket with status `OPEN`.
2. If it remains open for 30 seconds, the scheduler changes it to `EXPIRED`.
3. An expired ticket is removed 3 minutes after its recorded expiration time.
4. An agent can change an open, unexpired ticket to `RESOLVED`; resolved tickets are not removed by the expired-ticket cleanup job.

Scheduled jobs run with a 1-second fixed delay, so transitions may occur slightly after their deadlines.

## Auditing

- `ticket_status_logs` records ticket creation, status transitions, and removal events.
- `login_history_logs` records login attempts and their success status.
- Only `MONITOR` users may read audit endpoints.
- Ticket audit records are stored separately from tickets and remain available after ticket removal.

**Retention note:** The application does not expose audit deletion endpoints. For a production-grade guarantee of permanent retention, database-level write restrictions, backups, and retention controls should also be enforced.

## Authorization Behavior

- `401 Unauthorized`: missing, invalid, or expired authentication token.
- `403 Forbidden`: authenticated user lacks the required role.
- `400 Bad Request`: invalid ticket text.
- `409 Conflict`: attempted change to a ticket that is not open or is past its deadline.

## Manual Verification

The following scenarios were exercised during development using Postman and MySQL:

- Customer creates a valid ticket (`201 Created`).
- Invalid ticket text with digits/symbols is rejected (`400`).
- A 250-character ticket is accepted; 251 characters are rejected.
- Agent lists tickets (`200`) and resolves an open ticket (`200`).
- Customer is denied ticket listing (`403`).
- Agent is denied audit-log access (`403`).
- Ticket expiration and removal are reflected in status audit logs.
- Monitor can retrieve login and ticket-status audit logs.

## Notes

- Demo users may be initialized by the application's `SeedUsers` component. Review that component for the actual usernames and credentials before documenting or sharing test accounts.
- The database schema is currently managed by Hibernate `ddl-auto: update` for local development. For production, use versioned database migrations and least-privilege database credentials.
