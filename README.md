# FleetOps

FleetOps is a backend service for managing customers and shipments in a small logistics system.

The project focuses on backend concerns that usually appear in production services: domain rules, database migrations, authentication, authorization, API documentation, automated tests, containerization, and CI.

## What the service does

FleetOps provides an HTTP API for:

- creating and retrieving customers;
- creating and retrieving shipments;
- filtering shipments by status and customer;
- paginating and sorting shipment results;
- changing shipment status according to the allowed lifecycle;
- authenticating users with JWT;
- restricting write operations by role.

A shipment follows this lifecycle:

```text
CREATED -> PLANNED -> IN_TRANSIT -> DELIVERED
```

Cancellation is also supported where allowed by the domain rules. Invalid transitions are rejected before the entity is persisted.

## Stack

- Java 21
- Spring Boot 4
- Spring MVC
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- JWT
- PostgreSQL 16
- Flyway
- Maven
- JUnit 5
- Mockito
- MockMvc
- OpenAPI / Swagger UI
- Docker Compose
- GitHub Actions

## Project structure

The code is grouped by business feature rather than by technical layer at the root level.

```text
src/main/java/com/aitovavi/fleetops
├── auth
│   ├── api
│   └── application
├── common
│   ├── api
│   └── config
├── customer
│   ├── api
│   ├── application
│   ├── domain
│   └── infrastructure
└── shipment
    ├── api
    ├── application
    ├── domain
    └── infrastructure
```

Controllers only handle HTTP concerns and DTO mapping. Business operations live in application services, while shipment status rules are kept inside the domain model.

Database schema changes are managed by Flyway. Hibernate validates the schema on startup instead of creating or updating tables automatically.

## Running the project

### Docker Compose

The easiest way to start the service is with Docker Compose.

Requirements:

- Docker
- Docker Compose

Build and start PostgreSQL and the application:

```bash
docker compose up --build -d
```

Check container status:

```bash
docker compose ps
```

View application logs:

```bash
docker compose logs app --tail=100
```

Stop the containers:

```bash
docker compose down
```

The API will be available at:

```text
http://localhost:18081
```

The PostgreSQL port exposed to the host is `55432`.

### Local application with PostgreSQL in Docker

Start only PostgreSQL:

```bash
docker compose up -d postgres
```

Run the application on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On Linux or macOS:

```bash
./mvnw spring-boot:run
```

## Authentication

Most API endpoints require a Bearer token.

Request a token:

```http
POST /api/v1/auth/login
Content-Type: application/json
```

Example request:

```json
{
  "username": "admin",
  "password": "admin-change-me"
}
```

Example response:

```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer",
  "expiresInSeconds": 3600
}
```

Use the returned token in subsequent requests:

```http
Authorization: Bearer <jwt>
```

Default local users:

| Username | Password | Role |
|---|---|---|
| `admin` | `admin-change-me` | `ADMIN` |
| `user` | `user-change-me` | `USER` |

These credentials and the default JWT secret are provided for local development only. They must be replaced through environment variables outside the local environment.

### Permissions

| Operation | USER | ADMIN |
|---|:---:|:---:|
| Read customers | Yes | Yes |
| Read shipments | Yes | Yes |
| Create customers | No | Yes |
| Create shipments | No | Yes |
| Change shipment status | No | Yes |

## API

### Authentication

```text
POST /api/v1/auth/login
```

### Customers

```text
POST /api/v1/customers
GET  /api/v1/customers
GET  /api/v1/customers/{id}
```

### Shipments

```text
POST  /api/v1/shipments
GET   /api/v1/shipments
GET   /api/v1/shipments/{id}
PATCH /api/v1/shipments/{id}/status
```

Shipment list examples:

```text
GET /api/v1/shipments?status=DELIVERED
GET /api/v1/shipments?customerId={customerId}
GET /api/v1/shipments?page=0&size=10
GET /api/v1/shipments?page=0&size=10&sort=createdAt,desc
```

The list response contains page metadata:

```json
{
  "content": [],
  "page": 0,
  "size": 10,
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": true
}
```

## API documentation

Swagger UI:

```text
http://localhost:18081/swagger-ui/index.html
```

OpenAPI document:

```text
http://localhost:18081/v3/api-docs
```

Health endpoint:

```text
http://localhost:18081/actuator/health
```

The health endpoint includes database connectivity, liveness, and readiness information.

## Configuration

The application can be configured through environment variables.

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://127.0.0.1:55432/fleetops` | PostgreSQL JDBC URL |
| `DB_USERNAME` | `fleetops` | Database username |
| `DB_PASSWORD` | `fleetops` | Database password |
| `SERVER_PORT` | `18081` | HTTP port |
| `JWT_SECRET` | local development value | HMAC signing key |
| `JWT_ISSUER` | `fleetops` | Expected JWT issuer |
| `JWT_TTL_SECONDS` | `3600` | Access token lifetime |
| `ADMIN_USERNAME` | `admin` | Local admin username |
| `ADMIN_PASSWORD` | `admin-change-me` | Local admin password |
| `USER_USERNAME` | `user` | Local user username |
| `USER_PASSWORD` | `user-change-me` | Local user password |

## Tests

Run the complete test suite:

```powershell
.\mvnw.cmd --batch-mode clean verify
```

The current suite covers:

- shipment lifecycle rules;
- valid and invalid status transitions;
- shipment service behavior;
- customer service behavior;
- duplicate customer validation;
- missing entity handling;
- authentication;
- unauthenticated access;
- USER and ADMIN permissions.

The build currently runs 22 tests.

## CI

The GitHub Actions workflow runs the full Maven build for pushes and pull requests targeting `main`.

The CI command is the same command used locally:

```bash
./mvnw --batch-mode clean verify
```

## Current limitations

The authentication users are stored in memory. This keeps the example focused on JWT issuance and role-based access, but it is not intended to represent production identity management.

Other possible extensions include:

- persistent users and refresh tokens;
- shipment history and audit events;
- integration tests with Testcontainers;
- event publishing for shipment status changes;
- centralized logging and metrics;
- deployment configuration for a cloud environment.

## Author

Avi Aitov