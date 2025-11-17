# Banking Microservices System

A reactive microservices-based banking system built with Spring Boot 3, WebFlux, and PostgreSQL.

## Prerequisites

Before running this application, ensure you have the following installed:

- **Java 21 (LTS)** - Required for building and running the services
- **Docker** - For containerized deployment
- **Docker Compose** - For orchestrating multi-container setup
- **Gradle** (optional) - Included via Gradle Wrapper (`./gradlew`)

### Verify Installation

```bash
# Check Java version
java -version  # Should show version 21.x.x

# Check Docker
docker --version
docker compose version
```

## Architecture

This system follows a **microservices architecture** with **hexagonal (ports and adapters) pattern** for each service.

### System Components

```
┌─────────────────────────────────────────────────────────────┐
│                         Gateway (8080)                       │
│                    Spring Cloud Gateway                      │
└────────────┬──────────────────────────────┬─────────────────┘
             │                              │
             ▼                              ▼
    ┌────────────────┐            ┌────────────────┐
    │ Person Service │            │Account Service │
    │    (8082)      │            │    (8081)      │
    │                │            │                │
    │ • Client Mgmt  │            │ • Accounts     │
    │ • Person Info  │            │ • Transactions │
    └────────┬───────┘            └────────┬───────┘
             │                              │
             ▼                              ▼
    ┌────────────────┐            ┌────────────────┐
    │  PostgreSQL    │            │  PostgreSQL    │
    │  person-db     │            │  account-db    │
    │   (5433)       │            │   (5432)       │
    └────────────────┘            └────────────────┘
             │                              │
             └──────────┬───────────────────┘
                        ▼
                ┌────────────────┐
                │   RabbitMQ     │
                │  (5672/15672)  │
                └────────────────┘
```

### Service Responsibilities

**Gateway Service (Port 8080)**
- API routing and load balancing
- Single entry point for all client requests
- Routes to person-service and account-service

**Person Service (Port 8082)**
- Manages clients and personal information
- Handles authentication (password hashing)
- Client lifecycle operations (CRUD)
- Database: `person-db` on port 5433

**Account Service (Port 8081)**
- Manages bank accounts and balances
- Processes transactions (deposits, withdrawals, transfers)
- Transaction history and reporting
- Database: `account-db` on port 5432

**RabbitMQ (Ports 5672/15672)**
- Message broker for async communication between services
- Management UI available at http://localhost:15672
- Credentials: guest/guest

### Hexagonal Architecture

Each service follows the **ports and adapters pattern**:

```
{service}/
├── domain/                    # Core business logic
│   ├── model/                 # Domain entities (Client, Account)
│   ├── exception/             # Business exceptions
│   ├── port/in/               # Input ports (use case interfaces)
│   └── port/out/              # Output ports (repository interfaces)
├── application/               # Use case orchestration
│   └── usecase/{feature}/     # Use case implementations
├── adapter/                   # External integrations
│   ├── in/web/                # REST controllers, DTOs
│   └── out/persistence/       # R2DBC repositories
└── config/                    # Spring configuration
```

**Key Principles:**
- Dependencies flow inward: `adapter` → `application` → `domain`
- Domain layer has no framework dependencies
- Each service owns its database (no shared databases)
- Reactive programming with Project Reactor (Mono/Flux)

### Technology Stack

- **Java 21** with **Spring Boot 3.5.7**
- **Spring WebFlux** - Reactive web framework
- **Spring Data R2DBC** - Reactive database access
- **PostgreSQL 16** - Relational database
- **Flyway** - Database migrations
- **RabbitMQ** - Message broker
- **Lombok** - Boilerplate reduction
- **Gradle** - Build automation

## Getting Started

### Quick Start with Docker Compose

The easiest way to run the entire system is using Docker Compose:

```bash
# Start all services (databases, RabbitMQ, microservices, gateway)
docker compose up -d

# Check service status
docker compose ps

# View logs
docker compose logs -f

# Stop all services
docker compose down

# Stop and remove volumes (clean slate)
docker compose down -v
```

### What Happens During Startup

1. **Infrastructure Services Start First:**
   - PostgreSQL databases (person-db, account-db)
   - RabbitMQ message broker
   - Health checks ensure they're ready

2. **Application Services Build and Start:**
   - Each service is built from source using Dockerfile
   - Flyway migrations run automatically on startup
   - Services wait for database health checks

3. **Gateway Starts Last:**
   - Routes are configured to forward requests to services

### Service Endpoints

Once running, access the services at:

- **Gateway**: http://localhost:8080
- **Person Service**: http://localhost:8082
- **Account Service**: http://localhost:8081
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)

### API Examples

```bash
# Create a client (via gateway)
curl -X POST http://localhost:8080/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pedro Rey",
    "gender": "male",
    "birthDate": "1997-04-28",
    "identification": "11111111",
    "address": "Cra 22 # 98 - 67",
    "password": "@Pedro1234",
    "phone": "3172839232",
    "status": true
}'

# Get all clients
curl http://localhost:8080/clientes

# Create an account
curl -X POST http://localhost:8080/cuentas \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "ACC-000222",
    "accountType": "AHORROS",
    "initialBalance": 2000,
    "status": true,
    "clientId": "2",
    "clientName": "Julian Martinez Rangel",
    "clientStatus": true
}'
```

## Development

### Running Individual Services Locally

Each service can be run independently for development:

```bash
# Navigate to service directory
cd person-service

# Run with local PostgreSQL (starts via Docker Compose)
./gradlew bootRun

# Run tests
./gradlew test

# Build without tests
./gradlew build -x test
```

### Database Access

Connect to databases directly:

```bash
# Person database
docker exec -it person-db psql -U myuser -d mydatabase

# Account database
docker exec -it account-db psql -U postgres -d accountdb
```

## Project Structure

```
.
├── person-service/          # Client and person management
├── account-service/         # Account and transaction management
├── gateway/                 # API Gateway
├── compose.yaml             # Docker Compose configuration
├── dev-up.sh               # Development startup script
└── README.md               # This file
```

## Troubleshooting

**Services won't start:**
```bash
# Check logs for specific service
docker compose logs person-service
docker compose logs account-service

# Restart a specific service
docker compose restart person-service
```

**Port conflicts:**
- Ensure ports 8080, 8081, 8082, 5432, 5433, 5672, 15672 are available
- Modify `compose.yaml` if you need different ports

**Database connection issues:**
- Wait for health checks to complete (can take 30-60 seconds)
- Check database logs: `docker compose logs person-db`

**Clean restart:**
```bash
# Remove everything and start fresh
docker compose down -v
docker compose up -d --build
```

## License

This project is part of a technical assessment for Devsu.
