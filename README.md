# Restaurant Microservices

A modern, production-ready microservices architecture for restaurant management built with **Spring Boot 3.5**, **Java 21**, and **Spring Cloud 2025**.

---

## Table of Contents

- [Architecture Overview](#-architecture-overview)
- [Tech Stack](#-tech-stack)
- [Services](#-services)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [API Gateway](#-api-gateway)
- [Configuration](#-configuration)
- [Docker Deployment](#-docker-deployment)
- [Development](#-development)

---

##  Architecture Overview

```
                                    ┌─────────────────┐
                                    │  Eureka Server  │
                                    │    (8761)       │
                                    └────────┬────────┘
                                             │
┌─────────────────────────────────────────────────────────────────────────┐
│                           API Gateway (8080)                            │
└──────────┬─────────┬─────────┬─────────┬─────────┬──────────────────────┘
           │         │         │         │         │         
     ┌─────┴─────┐   │   ┌─────┴─────┐   │   ┌─────┴─────┐   
     │   Auth    │   │   │   Menu    │   │   │  Profile  │   
     │  Service  │   │   │  Service  │   │   │  Service  │   
     │  (8081)   │   │   │  (8082)   │   │   │  (8084)   │   
     └───────────┘   │   └───────────┘   │   └───────────┘   
                     │                   │                   
               ┌─────┴─────┐       ┌─────┴─────┐       
               │   Order   │       │Reservation│       
               │  Service  │       │  Service  │       
               │  (8085)   │       │  (8086)   │     
               └───────────┘       └───────────┘      
                     │                   │
              ┌──────┴───────────────────┴──────┐
              │                                 │
        ┌─────┴─────┐    ┌──────────┐    ┌─────┴─────┐
        │  Kafka    │    │   Redis  │    │ PostgreSQL│
        │  (9092)   │    │  (6379)  │    │  (5432)   │
        └───────────┘    └──────────┘    └───────────┘
```

---

## ️ Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.5.7, Spring Cloud 2025.0.0 |
| **Service Discovery** | Netflix Eureka |
| **API Gateway** | Spring Cloud Gateway (WebFlux) |
| **Database** | PostgreSQL 16 |
| **Caching** | Redis 7 |
| **Messaging** | Apache Kafka 3.7 |
| **Security** | Spring Security, JWT (JJWT 0.12.3) |
| **Containerization** | Docker, Docker Compose |
| **Build Tool** | Gradle 8.5 |

---

##  Services

### Infrastructure Services

| Service | Port | Description |
|---------|------|-------------|
| **Eureka Server** | 8761 | Service discovery and registration |
| **Config Service** | - | Centralized configuration management |
| **API Gateway** | 8090 | Single entry point, routing, and security |

### Business Services

| Service | Port | Description |
|---------|------|-------------|
| **Auth Service** | 8081 | Authentication & authorization (JWT) |
| **Menu Service** | 8082 | Menu items, categories, and pricing |
| **Order Service** | 8083 | Order management and processing |
| **Profile Service** | 8084 | User profiles and preferences |
| **Reservation Service** | 8085 | Table reservation management |
| **Table Service** | - | Restaurant table management |

### Shared Modules

| Module | Description |
|--------|-------------|
| `common-module/data` | Shared data models and DTOs |
| `common-module/utils` | Common utility functions |
| `common-module/redis-module` | Redis caching utilities |
| `common-module/kafka-module` | Kafka messaging utilities |
| `common-module/security-module` | JWT and security utilities |
| `common-module/factory-module` | Factory patterns and builders |

---

##  Prerequisites

- **Java 21** or higher
- **Docker** and **Docker Compose**
- **Gradle 8.5** (or use included wrapper)

---

##  Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd VNPAY_Microservices
```

### 2. Start Infrastructure with Docker

```bash
# Start all services
docker-compose up -d

# Or start only infrastructure (database, redis, kafka)
docker-compose up -d postgres redis kafka
```

### 3. Run Services Locally

```bash
# Build all services
./gradlew clean build -x test

# Run a specific service
./gradlew :auth-service:bootRun
./gradlew :menu-service:bootRun
# ... etc
```

### 4. Verify Services

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8090/actuator/health

---

##  Project Structure

```
Restaurant_Microservices/
├── api-gateway/            # API Gateway (Spring Cloud Gateway)
├── auth-service/           # Authentication service
├── menu-service/           # Menu management service
├── order-service/          # Order processing service
├── profile-service/        # User profile service
├── reservation-service/    # Reservation management
├── config-service/         # Config server
├── eureka-server/          # Service discovery
├── common-module/          # Shared libraries
│   ├── data/               # Shared entities & DTOs
│   ├── utils/              # Utility classes
│   ├── redis-module/       # Redis caching
│   ├── kafka-module/       # Kafka messaging
│   ├── security-module/    # Security & JWT
│   └── factory-module/     # Factory patterns
├── docker-compose.yml      # Docker orchestration
├── Dockerfile              # Multi-stage build
├── build.gradle            # Root Gradle config
└── settings.gradle         # Module includes
```

---

## 🌐 API Gateway

All requests are routed through the API Gateway on port `8090`.

### Base Routes

| Path | Service | Description |
|------|---------|-------------|
| `/api/auth/**` | auth-service | Authentication endpoints |
| `/api/menu/**` | menu-service | Menu management |
| `/api/orders/**` | order-service | Order operations |
| `/api/profiles/**` | profile-service | User profiles |
| `/api/reservations/**` | reservation-service | Reservations |

---

## ⚙️ Configuration

### Environment Variables

Configuration is managed via environment variables and Spring Cloud Config. Key variables:

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/db_name
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Eureka
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/
```

### Database Setup

Each service requires its own database:

```sql
CREATE DATABASE auth_service_db;
CREATE DATABASE menu_service_db;
CREATE DATABASE order_service_db;
CREATE DATABASE profile_service_db;
CREATE DATABASE reservation_service_db;
```

---

## 🐳 Docker Deployment

### Build All Services

```bash
# Build specific service
docker build --build-arg SERVICE_NAME=auth-service -t auth-service .

# Start all services
docker-compose up -d
```

### Service Health Checks

All services include health checks accessible at:
```
http://localhost:<port>/actuator/health
```

### Docker Compose Services

| Container | Port | Purpose |
|-----------|------|---------|
| postgres | 5432 | PostgreSQL database |
| redis | 6379 | Redis cache |
| kafka | 9092 | Message broker |
| eureka-server | 8761 | Service discovery |
| api-gateway | 8090 | API routing |

---

## 💻 Development

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests for specific service
./gradlew :auth-service:test
```

### Code Style

The project follows standard Java conventions with Lombok for boilerplate reduction.

### Adding a New Service

1. Create service directory
2. Add to `settings.gradle`
3. Configure `build.gradle`
4. Add Dockerfile build arg
5. Add to `docker-compose.yml`

---

##  License

This project is proprietary to Vo Nguyen Kien .

---


