# LTAPP - Load Testing Application

## Overview

LTAPP (Load Testing Application) is a Spring Boot-based REST API designed as a **system-under-test** for load testing training. It provides a realistic, production-like application with authentication, document management, message queuing, and observability features that allow students to practice building comprehensive load testing scenarios.

The application is built with modern Java and Spring Boot best practices, featuring a clean layered architecture, comprehensive error handling, and extensive configuration options suitable for various testing environments.

## Purpose

LTAPP serves as a training platform where students can:
- Practice load testing with realistic business scenarios
- Learn to validate API responses beyond HTTP status codes
- Understand authentication flows in load tests
- Work with data-driven testing using datapools
- Observe application behavior under load through metrics
- Configure and deploy applications using environment variables

## Technology Stack

- **Java 21** - Modern Java features and performance
- **Spring Boot 3.2.2** - Application framework
- **Spring Data JPA** - Database persistence layer
- **PostgreSQL 16** - Relational database
- **Flyway 10.7.1** - Database migration management
- **Apache Kafka** - Message broker for asynchronous communication
- **Spring Security** - Authentication and authorization
- **JWT (JSON Web Tokens)** - Stateless authentication
- **Micrometer + Prometheus** - Application metrics and monitoring
- **SpringDoc OpenAPI 3** - API documentation (Swagger UI)
- **Maven** - Build and dependency management

## Architecture

### Layered Architecture

The application follows a clean, layered architecture pattern:

```
┌─────────────────────────────────────────┐
│         Controllers Layer                │
│  (REST endpoints, request validation)     │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│          Service Layer                   │
│  (Business logic, orchestration)         │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│        Repository Layer                   │
│  (Data access, Spring Data JPA)          │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Database Layer                   │
│  (PostgreSQL via Flyway migrations)      │
└─────────────────────────────────────────┘
```

### Package Structure

```
org.example/
├── Main.java                          # Application entry point
├── config/                            # Configuration classes
│   └── SeedProperties.java            # Database seeding configuration
├── controllers/                       # REST controllers
│   ├── AuthController.java           # Authentication endpoints
│   ├── DocsController.java           # Document management endpoints
│   ├── KafkaController.java          # Kafka message endpoints
│   ├── AdminDataController.java      # Datapool generation endpoints
│   ├── ConfigureController.java      # Load control endpoints
│   └── WebController.java            # Legacy signing endpoint
├── service/                          # Business logic services
│   ├── AuthService.java              # Authentication logic
│   ├── DocService.java               # Document operations
│   ├── KafkaMessageService.java      # Kafka message handling
│   ├── AdminDataService.java         # Datapool generation
│   ├── LoadControlService.java       # CPU/memory load control
│   └── DatabaseSeeder.java           # Runtime data seeding
├── database/                         # Data persistence
│   ├── entity/                       # JPA entities
│   │   ├── UserEntity.java           # User authentication entity
│   │   └── DocEntity.java            # Document entity
│   └── repository/                   # Spring Data repositories
│       ├── UserRepository.java
│       └── DocsRepository.java
├── dto/                              # Data Transfer Objects
│   ├── ApiResponse.java              # Unified response wrapper
│   ├── DocResponse.java              # Document response DTOs
│   ├── AuthResponse.java             # Authentication response
│   └── ...                           # Other request/response DTOs
├── security/                         # Security configuration
│   ├── SecurityConfig.java           # Spring Security setup
│   ├── JwtService.java               # JWT token operations
│   └── JwtAuthenticationFilter.java  # JWT authentication filter
├── exception/                        # Exception handling
│   ├── GlobalExceptionHandler.java   # Global error handler
│   ├── EntityNotFoundException.java  # Custom exceptions
│   └── AuthenticationExceptionHandler.java # Auth error handler
└── metrics/                          # Observability
    └── TimedConfiguration.java      # Micrometer timing configuration
```

### Key Design Patterns

- **Service Layer Pattern**: Business logic separated from controllers
- **DTO Pattern**: Data Transfer Objects for request/response mapping
- **Repository Pattern**: Data access abstraction via Spring Data JPA
- **Strategy Pattern**: Configurable behavior via environment variables
- **Factory Pattern**: `ApiResponse` factory methods for consistent responses
- **Filter Pattern**: JWT authentication via servlet filter

## Core Features

### 1. Authentication & Authorization

**JWT-based Stateless Authentication**

- User registration and login endpoints
- Password hashing with BCrypt
- Role-based access control (ROLE_USER, ROLE_ADMIN)
- Stateless JWT tokens with configurable expiration
- Protected business endpoints requiring authentication

**Endpoints:**
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Authenticate and receive JWT token

**Public Endpoints:**
- `/api/auth/**` - Authentication endpoints
- `/actuator/**` - Health and metrics endpoints
- `/swagger-ui/**`, `/v3/api-docs/**` - API documentation

### 2. Document Management

**Complete Document Lifecycle**

- Document upload with metadata tracking
- Document search with pagination
- Document signing workflow (UPLOADED → SIGNED)
- Document deletion
- Version tracking for optimistic locking
- Configurable processing delays for load testing

**Endpoints:**
- `POST /api/docs` - Upload document
- `GET /api/docs/{id}` - Get document details
- `GET /api/docs/search` - Search documents with pagination
- `POST /api/docs/{id}/sign` - Sign existing document
- `DELETE /api/docs/{id}` - Delete document
- `POST /api/signDoc` - Legacy signing endpoint (backward compatible)

**Document Status Workflow:**
```
UPLOADED → SIGNED
```

### 3. Kafka Message Processing

**Asynchronous Message Handling**

- Send messages to Kafka topics
- Internal message queue for consumption
- Message statistics and monitoring
- Random message retrieval for testing

**Endpoints:**
- `POST /api/messages` - Send message to Kafka
- `GET /api/messages/random` - Get random message from queue
- `GET /api/messages/stats` - Get Kafka statistics
- `GET /api/messages/count` - Get current queue size

**Legacy Endpoints:**
- `GET /api/sendMessage/{message}` - Legacy message sending
- `GET /api/getMessage` - Legacy message retrieval

### 4. Datapool Generation

**Server-Side Test Data Generation**

- Bulk document generation with configurable parameters
- Bulk Kafka message generation
- Datapool retrieval for external test tools
- Admin-only endpoints for data management

**Endpoints:**
- `POST /api/admin/datapools/docs` - Generate documents in bulk
- `GET /api/admin/datapools/docs` - Get document datapool
- `POST /api/admin/datapools/messages` - Generate messages in bulk
- `GET /api/admin/datapools/messages` - Get message datapool

### 5. Load Control

**Resource Stress Testing**

- CPU load generation with configurable thread count
- Memory leak simulation for testing
- Start/stop controls for load scenarios

**Endpoints:**
- `GET /api/startCPULoad` - Start CPU load generation
- `GET /api/stopCPULoad` - Stop CPU load
- `GET /api/startLeak` - Start memory leak simulation
- `GET /api/stopLeak` - Stop memory leak

### 6. Observability

**Metrics and Monitoring**

- Micrometer integration with Prometheus
- Custom timers for business operations
- Actuator endpoints for health and metrics
- Structured logging throughout the application

**Available Endpoints:**
- `/actuator/health` - Application health status
- `/actuator/prometheus` - Prometheus metrics export
- `/actuator/metrics` - Available metrics list

### 7. Database Seeding

**Runtime Test Data Generation**

- Automatic data generation on application startup
- Configurable user and document counts
- Random but realistic test data
- Only seeds empty databases (safety check)

**Configuration:**
- `LTAPP_SEED_ENABLED` - Enable/disable seeding
- `LTAPP_SEED_USERS` - Number of users to generate
- `LTAPP_SEED_DOCS_PER_USER` - Documents per user
- `LTAPP_SEED_MAX_DOC_VERSION` - Maximum document version
- `LTAPP_SEED_DAYS_RANGE` - Timestamp range for random dates

## API Response Format

All API responses follow a consistent JSON structure:

**Success Response:**
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    // Response-specific data
  },
  "errors": [],
  "timestamp": "2025-12-06T10:00:00Z",
  "statusCode": 200
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Operation failed",
  "data": null,
  "errors": [
    {
      "code": "VALIDATION_ERROR",
      "message": "Field validation failed",
      "field": "username"
    }
  ],
  "timestamp": "2025-12-06T10:00:00Z",
  "statusCode": 400
}
```

This consistent format allows students to validate responses beyond HTTP status codes, checking `success` flags, `data` structure, and specific field values.

## Database Schema

### Users Table
Stores user authentication information:
- `id` (SERIAL PRIMARY KEY)
- `username` (VARCHAR(50), UNIQUE, NOT NULL)
- `password_hash` (VARCHAR(255), NOT NULL)
- `email` (VARCHAR(255), UNIQUE)
- `role` (VARCHAR(50), NOT NULL, DEFAULT 'ROLE_USER')
- `created_at` (TIMESTAMP WITH TIME ZONE, NOT NULL)

### Documents Table
Stores document metadata and content:
- `id` (SERIAL PRIMARY KEY)
- `name` (VARCHAR(255), NOT NULL)
- `document` (BYTEA, NOT NULL) - Binary document content
- `status` (VARCHAR(20), NOT NULL, DEFAULT 'UPLOADED') - UPLOADED or SIGNED
- `version` (INTEGER, NOT NULL, DEFAULT 1) - Optimistic locking
- `uploaded_by` (VARCHAR(100))
- `created_at` (TIMESTAMP WITH TIME ZONE, NOT NULL)

### Schema Management

The application uses **Flyway** for database schema versioning:
- Initial migration: `V1__init_schema.sql`
- Automatic schema creation on fresh databases
- Version-controlled migrations
- No runtime schema generation (production-ready)

## Configuration

### Environment Variables

All critical settings are configurable via environment variables:

**Database:**
- `LTAPP_DB_URL` - Database connection URL
- `LTAPP_DB_USER` - Database username
- `LTAPP_DB_PASSWORD` - Database password

**Kafka:**
- `LTAPP_KAFKA_BOOTSTRAP_SERVERS` - Kafka bootstrap servers
- `LTAPP_KAFKA_TOPIC` - Kafka topic name

**Authentication:**
- `LTAPP_AUTH_JWT_SECRET` - JWT secret key (required in production)
- `LTAPP_AUTH_JWT_EXPIRATION` - Token expiration in seconds

**Application:**
- `LTAPP_SERVER_PORT` - Server port (default: 8080)
- `LTAPP_SIGN_DELAY_MS` - Artificial delay for signing operations
- `LTAPP_LOAD_CPU_THREADS` - CPU load generation thread count
- `LTAPP_LOAD_LEAK_STEP_BYTES` - Memory leak step size

**Database Seeding:**
- `LTAPP_SEED_ENABLED` - Enable/disable seeding (default: false)
- `LTAPP_SEED_USERS` - Number of users to generate
- `LTAPP_SEED_DOCS_PER_USER` - Documents per user
- `LTAPP_SEED_MAX_DOC_VERSION` - Maximum document version
- `LTAPP_SEED_DAYS_RANGE` - Days range for random timestamps

**Flyway:**
- `LTAPP_FLYWAY_ENABLED` - Enable/disable Flyway (default: true)
- `LTAPP_FLYWAY_BASELINE_ON_MIGRATE` - Baseline existing schema (default: false)
- `LTAPP_FLYWAY_VALIDATE_ON_MIGRATE` - Validate migrations (default: true)

### Configuration Files

- `application.yaml` - Main configuration file with environment variable placeholders
- `src/main/resources/db/migration/V1__init_schema.sql` - Initial database schema

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL 16 (running and accessible)
- Apache Kafka (optional, for message features)

### Database Setup

1. **Create PostgreSQL database:**
   ```sql
   CREATE DATABASE ltapp;
   ```

2. **Configure connection** via environment variables or `application.yaml`

### Running the Application

1. **Set environment variables** (optional, defaults provided):
   ```bash
   export LTAPP_DB_URL=jdbc:postgresql://localhost:5432/postgres
   export LTAPP_DB_USER=postgres
   export LTAPP_DB_PASSWORD=your_password
   export LTAPP_AUTH_JWT_SECRET=your-secret-key-min-32-chars
   ```

2. **Build and run:**
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

3. **Verify startup:**
   - Application starts on port 8080 (configurable)
   - Flyway migrations execute automatically
   - Health endpoint: `http://localhost:8080/actuator/health`

### Enabling Database Seeding

To populate the database with test data on startup:

```bash
export LTAPP_SEED_ENABLED=true
export LTAPP_SEED_USERS=10
export LTAPP_SEED_DOCS_PER_USER=50
mvn spring-boot:run
```

Seeded users have predictable passwords: `student1pass`, `student2pass`, etc.

## API Documentation

Interactive API documentation is available via Swagger UI:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

The documentation includes:
- All available endpoints
- Request/response schemas
- Authentication requirements
- Example requests and responses
- Try-it-out functionality

## Testing Scenarios

### Basic Load Test Flow

1. **Authentication:**
   ```
   POST /api/auth/register → Create test user
   POST /api/auth/login → Get JWT token
   ```

2. **Document Operations:**
   ```
   POST /api/docs → Upload document
   GET /api/docs/{id} → Verify upload
   POST /api/docs/{id}/sign → Sign document
   GET /api/docs/{id} → Verify status = "SIGNED"
   ```

3. **Message Operations:**
   ```
   POST /api/messages → Send message
   GET /api/messages/stats → Check statistics
   GET /api/messages/random → Retrieve message
   ```

4. **Validation:**
   - Check `success == true` in responses
   - Validate `data.status == "SIGNED"`
   - Verify `data.id` is not null
   - Check `data.processingTimeMs > 0` for signed documents

### Data-Driven Testing

1. **Generate Datapool:**
   ```
   POST /api/admin/datapools/docs?count=1000
   GET /api/admin/datapools/docs?limit=100
   ```

2. **Use in Load Test:**
   - Extract document IDs from datapool response
   - Use IDs in test scenarios
   - Validate responses match datapool data

## Monitoring

### Prometheus Metrics

Metrics are exposed at `/actuator/prometheus`:

- HTTP request metrics (count, duration, percentiles)
- Custom business metrics (document upload, signing, etc.)
- JVM metrics (memory, threads, GC)
- Database connection pool metrics

### Health Checks

Health endpoint at `/actuator/health` provides:
- Application status (UP/DOWN)
- Database connectivity
- Component health status

## Security

### Authentication Flow

1. User registers via `POST /api/auth/register`
2. User logs in via `POST /api/auth/login`
3. Application returns JWT token in response
4. Client includes token in `Authorization: Bearer <token>` header
5. `JwtAuthenticationFilter` validates token and sets authentication context

### Password Security

- Passwords are hashed using BCrypt
- Never stored in plain text
- Configurable password encoder strength

### Endpoint Protection

- Most business endpoints require authentication
- Admin endpoints may require ROLE_ADMIN
- Public endpoints: health checks, authentication, documentation

## Error Handling

### Global Exception Handler

All exceptions are handled by `GlobalExceptionHandler`:
- Validation errors → 400 Bad Request with field details
- Entity not found → 404 Not Found
- Authentication errors → 401 Unauthorized
- Authorization errors → 403 Forbidden
- Internal errors → 500 Internal Server Error

All errors return consistent JSON format using `ApiResponse` wrapper.

## Development

### Building

```bash
mvn clean compile        # Compile source code
mvn clean package        # Build JAR file
mvn spring-boot:run      # Run application
```

### Project Structure

- `src/main/java` - Source code
- `src/main/resources` - Configuration and resources
  - `application.yaml` - Application configuration
  - `db/migration/` - Flyway migration scripts
- `src/test/java` - Test code (if present)
- `pom.xml` - Maven build configuration

### Code Quality

- Layered architecture with clear separation of concerns
- DTOs for all external interfaces
- Comprehensive error handling
- Structured logging
- Configuration externalization
- Production-ready database migrations

## Production Considerations

### Database

- Use Flyway for all schema changes
- Never use `ddl-auto: update` in production
- Test migrations on staging first
- Use connection pooling (HikariCP configured)

### Security

- Set strong `LTAPP_AUTH_JWT_SECRET` (minimum 32 characters)
- Use HTTPS in production
- Configure proper CORS policies
- Review and restrict admin endpoints

### Monitoring

- Enable Prometheus metrics collection
- Set up alerting on health endpoints
- Monitor application logs
- Track custom business metrics

### Performance

- Configure appropriate connection pool sizes
- Tune JVM parameters for your workload
- Monitor database query performance
- Use appropriate Kafka consumer configurations

## Example Load Testing Workflow

### 1. Setup and Authentication

```bash
# Register a test user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass123","email":"test@example.com"}'

# Login and extract token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass123"}' \
  | jq -r '.data.accessToken')
```

### 2. Document Operations

```bash
# Upload document
curl -X POST http://localhost:8080/api/docs \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@document.pdf"

# Sign document
curl -X POST "http://localhost:8080/api/docs/1/sign?signAlgorithm=RSA-256" \
  -H "Authorization: Bearer $TOKEN"

# Verify status
curl -X GET http://localhost:8080/api/docs/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 3. Response Validation

Students can validate responses using:
- `success == true` flag
- `data.status == "SIGNED"`
- `data.processingTimeMs > 0`
- `data.id` is not null
- `data.uploadedBy` matches authenticated user

## Troubleshooting

### Application Won't Start

**Database Connection Issues:**
- Verify PostgreSQL is running
- Check `LTAPP_DB_URL`, `LTAPP_DB_USER`, `LTAPP_DB_PASSWORD`
- Ensure database exists

**Flyway Migration Issues:**
- For existing databases: set `LTAPP_FLYWAY_BASELINE_ON_MIGRATE=true`
- For fresh databases: ensure Flyway is enabled
- Check migration files in `src/main/resources/db/migration/`

**Port Already in Use:**
- Change `LTAPP_SERVER_PORT` environment variable
- Or stop the process using port 8080

### Seeding Not Working

- Verify `LTAPP_SEED_ENABLED=true`
- Check database is empty enough (< 5 users, < 50 documents)
- Review application logs for seeding messages
- Ensure tables exist (Flyway migrations completed)

### Authentication Issues

- Verify JWT secret is set: `LTAPP_AUTH_JWT_SECRET`
- Check token expiration: `LTAPP_AUTH_JWT_EXPIRATION`
- Ensure token is included in `Authorization: Bearer <token>` header
- Verify user exists in database

## Additional Resources

- **Flyway Migration Guide**: See `FLYWAY_MIGRATION_GUIDE.md`
- **Database Seeding Guide**: See `DATABASE_SEEDING_GUIDE.md`
- **API Documentation**: Available at `/swagger-ui.html` when application is running

## License

This project is intended for educational purposes in load testing training.

## Support

For issues, questions, or contributions related to load testing scenarios, please refer to the project documentation or contact the training instructor.

