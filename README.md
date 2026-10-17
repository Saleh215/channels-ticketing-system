# Channels Ticketing Assessment (MVP)

## Requirements
Java 21, Maven 3.9+, PostgreSQL database named `ticketing`.

## Run
1. Create database: `CREATE DATABASE ticketing;`
2. Configure `DB_URL`, `DB_USER`, `DB_PASSWORD` environment variables, or edit `src/main/resources/application.yml`.
3. Set `JWT_SECRET` to a random secret with at least 32 bytes (do not commit real secrets).
4. Run `mvn spring-boot:run`.

## Seed accounts (local testing only)
- `customer` / `Customer123!`
- `agent` / `Agent123!`
- `monitor` / `Monitor123!`

## API
- `POST /api/auth/login` body `{"username":"customer","password":"Customer123!"}` => JWT (900 seconds).
- `POST /api/tickets` CUSTOMER, body `{"text":"Printer is broken"}`.
- `GET /api/tickets`, `GET /api/tickets/{id}` AGENT.
- `PATCH /api/tickets/{id}` AGENT, body `{"text":"Updated issue"}`.
- `PATCH /api/tickets/{id}/resolve` AGENT.
- `GET /api/audit/tickets`, `GET /api/audit/logins` MONITOR.

Send `Authorization: Bearer <token>` on protected requests.

## Assumptions
- Tickets start OPEN, expire 30 seconds after creation, and are deleted 3 minutes after entering EXPIRED.
- RESOLVED tickets remain stored and are not expired.
- Only letters and ASCII spaces are allowed in ticket text (Unicode letters supported).
- Scheduler runs every second; timings are approximate.
- Ticket audit entries are retained after deletion; deletion is recorded as EXPIRED -> DELETED.

## Production gaps
This is a time-boxed MVP, not production-ready: `ddl-auto:update` should be replaced with Flyway migrations; audit table permissions should be append-only and backups/retention policies enforced; scheduler concurrency should be coordinated across instances; authentication brute-force protection, structured error responses, paging, and automated tests should be added. The demo seed passwords and JWT fallback secret must be removed for deployment.
