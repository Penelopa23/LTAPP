# LTAPP - Load Testing Application

## 1. Project Overview

LTAPP is a Spring Boot 3.2.2 application built on Java 21, designed as a system-under-test for load testing and monitoring training. It provides a realistic REST API with authentication, document management, Kafka messaging, and comprehensive observability features. Students can use LTAPP as a target system for load testing tools like JMeter, k6, Locust, or Gatling.

The application uses modern Java and Spring Boot best practices, including layered architecture, DTOs, global exception handling, JWT-based authentication, Flyway database migrations, and structured logging. It integrates with PostgreSQL 15 for data persistence, Apache Kafka for message queuing, Prometheus for metrics collection, and Grafana for visualization.

## 2. Architecture

LTAPP consists of the following components:

- **Spring Boot Application** (main class: `org.example.Main`)
  - REST API with JWT authentication
  - Document management with file upload/download
  - Kafka producer and consumer
  - Spring Boot Actuator for metrics
  - Swagger/OpenAPI documentation

- **PostgreSQL 15 Database**
  - Database name: `ltapp`
  - User: `ltappadm`
  - Password: `ltappadm`
  - Schema managed by Flyway migrations
  - Tables: `users`, `documents`

- **Apache Kafka Broker** (latest image)
  - Topic: `ltapp-messages` (auto-created)
  - Ports: 9092 (PLAINTEXT), 9093 (CONTROLLER)

- **Prometheus** (latest image)
  - Scrapes metrics from `/actuator/prometheus`
  - Configuration: `monitoring/prometheus.yml`

- **Grafana** (latest image)
  - Default credentials: `admin` / `admin`
  - Pre-configured to use Prometheus as data source

### Architecture Flow

```
Client → LTAPP (REST + JWT)
         ↓
    PostgreSQL (JPA + Flyway)
         ↓
    Kafka (producer + consumer)
         ↓
/actuator/prometheus → Prometheus → Grafana
```

## 3. Prerequisites

- **Docker** and **Docker Compose** installed
- Optionally: **JDK 21** and **Maven** (only if you need to rebuild the application image locally)

Note: The Docker image uses `eclipse-temurin:21-jre-alpine` as the base image, which supports both x86_64 and ARM64 (Apple Silicon) architectures.

## 4. Quick Start with Docker Compose

### Step 1: Build the Application JAR

```bash
mvn clean package -DskipTests
```

This creates `target/LTAPP-1.0-SNAPSHOT.jar`.

### Step 2: Build the Docker Image

```bash
docker build -t ltapp:local .
```

### Step 3: Start All Services

```bash
docker compose up -d
```

This starts PostgreSQL, Kafka, LTAPP, Prometheus, and Grafana containers.

### Step 4: Verify Services

Wait a few seconds for all services to start, then check:

- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (default credentials: `admin` / `admin`)

### Step 5: Stop and Clean Up

```bash
docker compose down -v
```

The `-v` flag removes volumes (database data, Kafka data, Grafana data).

## 5. Configuration & Environment Variables

All configuration is driven by environment variables with sensible defaults. In Docker Compose, these are already configured, but you can override them.

### Database

- `LTAPP_DB_URL` - Database connection URL
  - Default: `jdbc:postgresql://localhost:55000/ltapp`
  - Docker: `jdbc:postgresql://postgres:5432/ltapp`
- `LTAPP_DB_USER` - Database username
  - Default: `postgres`
  - Docker: `ltappadm`
- `LTAPP_DB_PASSWORD` - Database password
  - Default: empty
  - Docker: `ltappadm`

In Docker Compose, the database connection is automatically wired to the `postgres` service.

### Kafka

- `LTAPP_KAFKA_BOOTSTRAP_SERVERS` - Kafka bootstrap servers
  - Default: `0.0.0.0:9092`
  - Docker: `kafka:9092`
- `LTAPP_KAFKA_TOPIC` - Kafka topic name
  - Default: `registration`
  - Docker: `ltapp-messages`

The Kafka container has `KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"`, so topics are created automatically on first use.

### JWT Authentication

- `LTAPP_AUTH_JWT_SECRET` - JWT secret key (minimum 32 characters)
  - Default: `dev-secret-key-change-in-production-min-32-chars`
  - **Important**: Change this in production!
- `LTAPP_AUTH_JWT_EXPIRATION` - Token expiration in seconds
  - Default: `3600` (1 hour)

JWT tokens are obtained via `POST /api/auth/login` and must be included in the `Authorization: Bearer <TOKEN>` header for protected endpoints.

### Signing & Load Simulation

- `LTAPP_SIGN_DELAY_MS` / `ltapp.sign.processing-delay-ms` - Artificial delay for document signing (milliseconds)
  - Default: `0`
- `LTAPP_LOAD_CPU_THREADS` / `ltapp.load.cpu-threads` - Number of threads for CPU load generation
  - Default: `15`
- `LTAPP_LOAD_LEAK_STEP_BYTES` / `ltapp.load.leak-step-bytes` - Memory leak step size in bytes
  - Default: `1024`

### Database Seeding (Test Data)

- `LTAPP_SEED_ENABLED` - Enable/disable database seeding
  - Default: `false`
  - Docker: `true`
- `LTAPP_SEED_USERS` - Number of users to generate
  - Default: `10`
- `LTAPP_SEED_DOCS_PER_USER` - Number of documents per user
  - Default: `50`
- `LTAPP_SEED_MAX_DOC_VERSION` - Maximum document version
  - Default: `5`
- `LTAPP_SEED_DAYS_RANGE` - Days range for random `createdAt` timestamps
  - Default: `30`

The seeder runs automatically at application startup if enabled and the database is empty enough (less than 5 users and 50 documents). It creates users with realistic names and documents with random content.

### Server Port

- `LTAPP_SERVER_PORT` - Server port
  - Default: `8080`

## 6. Authentication & Test Users

### Seeded Users

When database seeding is enabled, users are created with the following pattern:

- **Username**: `{firstName}_{lastName}_{number}` (e.g., `alex_smith_1`, `maria_johnson_2`)
- **Password**: `student{N}pass` where N is the user number (e.g., `student1pass`, `student2pass`)
- **Email**: `{username}@student.test`
- **Roles**: 90% `ROLE_USER`, 10% `ROLE_ADMIN` (randomly assigned)

Example users:
- Username: `alex_smith_1`, Password: `student1pass`
- Username: `maria_johnson_2`, Password: `student2pass`

### Login Example

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alex_smith_1",
    "password": "student1pass"
  }'
```

Response:
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600,
    "username": "alex_smith_1",
    "tokenType": "Bearer",
    "role": "ROLE_USER"
  },
  "errors": []
}
```

### Using JWT Token

```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/docs/search?name=contract&page=0&size=10
```

### Register New User

```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "email": "newuser@example.com"
  }'
```

## 7. REST API Overview

All business endpoints require JWT authentication (except `/api/auth/**` and `/actuator/**`). The best way to explore all endpoints is via Swagger UI at http://localhost:8080/swagger-ui/index.html.

### Authentication

- `POST /api/auth/register` - Register a new user (returns JWT)
- `POST /api/auth/login` - Authenticate and get JWT token

### Documents

- `POST /api/docs` - Upload a document (multipart/form-data, requires authentication)
  - Returns: `DocDetailsResponse` with `status="UPLOADED"`, `version=1`
- `GET /api/docs/{id}` - Get document metadata by ID
  - Returns: `DocDetailsResponse` with full metadata including status and version
- `GET /api/docs/search?name=...&page=...&size=...` - Search documents with pagination
  - Query parameters: `name` (required), `page` (default: 0), `size` (default: 10)
  - Returns: `PageDto<DocResponse>` with paginated results
- `DELETE /api/docs/{id}` - Delete a document by ID
  - Returns: `DeleteResponse` with `deleted=true` flag
- `POST /api/docs/{id}/sign` - Sign an existing document by ID
  - Request body (optional): `{"signAlgorithm": "FAKE-RSA", "comment": "..."}`
  - Query parameter (optional): `?signAlgorithm=FAKE-RSA`
  - Updates document status to `"SIGNED"` and increments version
  - Returns: `SignedDocResponse` with `processingTimeMs` for validation

### Kafka Messages

- `POST /api/messages` - Send a JSON message to Kafka topic
  - Request body: `{"payload": "message content"}`
  - Returns: `KafkaMessageResponse` with `status="ENQUEUED"`, `messageId`, `payloadLength`
- `GET /api/messages/random` - Get a random message from internal queue
  - Returns: `KafkaMessageResponse` with message details
- `GET /api/messages/count` - Get current queue size
  - Returns: `{"count": <number>, "queueName": "internal"}`
- `GET /api/messages/stats` - Get Kafka statistics
  - Returns: `KafkaStatsResponse` with `totalSent`, `totalConsumed`, `currentQueueSize`, `lastMessageTimestamp`
- `GET /api/getMessage` - Get next message from queue (legacy endpoint, still supported)

### Admin Data Pools

- `POST /api/admin/datapools/docs?count=...&namePrefix=...&minSizeBytes=...&maxSizeBytes=...` - Generate documents in bulk (requires ADMIN role)
  - Query parameters: `count` (required), `namePrefix` (default: "test_doc_"), `minSizeBytes` (default: 1024), `maxSizeBytes` (default: 10240)
  - Returns: `DatapoolGenerationResponse` with `createdCount` and `sampleIds`
- `GET /api/admin/datapools/docs?limit=...&status=...&namePrefix=...` - Get documents for datapool
  - Query parameters: `limit` (default: 100), `status` (optional), `namePrefix` (optional)
  - Returns: `List<DocResponse>` suitable for building external datapools
- `POST /api/admin/datapools/messages?count=...&pattern=...` - Generate Kafka messages in bulk (requires ADMIN role)
  - Query parameters: `count` (required), `pattern` (default: "test_message_{index}_{random}")
  - Returns: `DatapoolGenerationResponse` with `createdCount`
- `GET /api/admin/datapools/messages?limit=...` - Get messages for datapool
  - Query parameters: `limit` (default: 100)
  - Returns: `List<KafkaMessagePreview>` with message previews

### Load Simulation (Hidden from Swagger, requires ADMIN role)

- `GET /api/startLeak` - Enable memory leak (for training only)
- `GET /api/stopLeak` - Disable memory leak
- `GET /api/startCPULoad` - Enable CPU load generation (for training only)
- `GET /api/stopCPULoad` - Disable CPU load generation

These endpoints are designed for load testing training scenarios to observe system behavior under resource stress.

## 8. Metrics & Monitoring

### Spring Boot Actuator Endpoints

- `GET /actuator` - List all available actuator endpoints
- `GET /actuator/health` - Application health status
- `GET /actuator/prometheus` - Prometheus metrics (exposed in Prometheus format)

### Prometheus Configuration

Prometheus is configured via `monitoring/prometheus.yml`:

```yaml
scrape_configs:
  - job_name: 'ltapp'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['ltapp:8080']
```

Prometheus scrapes metrics every 5 seconds from `http://ltapp:8080/actuator/prometheus` (inside Docker network).

### Grafana Setup

1. Access Grafana at http://localhost:3000
2. Login with default credentials: `admin` / `admin`
3. Add Prometheus as a data source:
   - URL: `http://prometheus:9090` (from inside Docker network) or `http://localhost:9090` (from host)
   - Access: Server (default)
4. Create dashboards using PromQL queries

### Example PromQL Queries

- `http_server_requests_seconds_count` - Total HTTP request count
- `http_server_requests_seconds_sum` - Total request duration
- `jvm_memory_used_bytes` - JVM memory usage
- `jvm_gc_pause_seconds_count` - GC pause count
- `kafka_producer_record_send_total` - Kafka messages sent (if Kafka metrics are exposed)

## 9. Local Development without Docker

### Running PostgreSQL and Kafka Locally

You can run PostgreSQL and Kafka using Docker Compose while running the application locally:

```bash
# Start only PostgreSQL and Kafka
docker compose up -d postgres kafka

# Run the application from IDE or CLI
mvn spring-boot:run
```

### Required Environment Variables for Local Run

Set these environment variables before running the application:

```bash
export LTAPP_DB_URL="jdbc:postgresql://localhost:55000/ltapp"
export LTAPP_DB_USER="ltappadm"
export LTAPP_DB_PASSWORD="ltappadm"
export LTAPP_KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
export LTAPP_KAFKA_TOPIC="ltapp-messages"
export LTAPP_AUTH_JWT_SECRET="dev-secret-key-change-in-production-min-32-chars"
export LTAPP_AUTH_JWT_EXPIRATION="3600"
```

Or create an `application-local.yml` file in `src/main/resources/` with these values.

### Running from IDE

1. Set the active profile to `local` (if using `application-local.yml`)
2. Ensure PostgreSQL and Kafka are running (via Docker Compose or locally)
3. Run `org.example.Main` as a Java application

## 10. Troubleshooting

### Connection Refused to PostgreSQL

- **Symptom**: `Connection to 0.0.0.0:55000 refused`
- **Solution**: 
  - Check that the PostgreSQL container is running: `docker compose ps`
  - Verify environment variables match `docker-compose.yaml`:
    - `LTAPP_DB_URL` should be `jdbc:postgresql://postgres:5432/ltapp` (inside Docker) or `jdbc:postgresql://localhost:55000/ltapp` (from host)
    - `LTAPP_DB_USER` and `LTAPP_DB_PASSWORD` should match `POSTGRES_USER` and `POSTGRES_PASSWORD` in `docker-compose.yaml`

### Kafka UNKNOWN_TOPIC_OR_PARTITION

- **Symptom**: `UNKNOWN_TOPIC_OR_PARTITION` error when sending messages
- **Solution**: 
  - Ensure Kafka container is healthy: `docker compose logs kafka`
  - Topics are auto-created, but wait a few seconds after Kafka starts
  - Verify `LTAPP_KAFKA_BOOTSTRAP_SERVERS` is correct (`kafka:9092` in Docker, `localhost:9092` from host)

### Ports Already in Use

- **Symptom**: `Bind for 0.0.0.0:8080 failed: port is already allocated`
- **Solution**: 
  - Stop previous containers: `docker compose down`
  - Check for local services using ports 8080, 9090, 3000, 9092, 9093
  - Change ports in `docker-compose.yaml` if needed

### Flyway Migration Errors

- **Symptom**: `FlywayValidateException: Migrations have failed validation`
- **Solution**: 
  - For development/testing with existing schema, set `LTAPP_FLYWAY_VALIDATE_ON_MIGRATE=false`
  - For fresh databases, ensure `LTAPP_FLYWAY_BASELINE_ON_MIGRATE=false`
  - Drop and recreate the database if needed: `docker compose down -v && docker compose up -d`

### Application Not Starting

- **Symptom**: Application exits immediately or fails to start
- **Solution**: 
  - Check logs: `docker compose logs ltapp`
  - Verify all dependencies are running: `docker compose ps`
  - Ensure JAR file exists: `ls -la target/LTAPP-1.0-SNAPSHOT.jar`
  - Rebuild if needed: `mvn clean package -DskipTests && docker build -t ltapp:local .`

## 11. License / Usage Notice

This project is intended for **educational and load testing practice purposes only**. It is not designed for production use. The application includes intentional features for training scenarios (memory leaks, CPU load generation) that should never be enabled in production environments.

No LICENSE file is included in this repository. Use at your own risk for educational purposes.
