# Multi-stage Dockerfile for all microservices
# Usage: docker build --build-arg SERVICE_NAME=auth-service -t auth-service .

# Stage 1: Build
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# Copy Gradle configuration files
COPY settings.gradle build.gradle gradlew ./
COPY gradle ./gradle

# Copy all source code
COPY common-module ./common-module
COPY api-gateway ./api-gateway
COPY auth-service ./auth-service
COPY config-service ./config-service
COPY eureka-server ./eureka-server
COPY menu-service ./menu-service
COPY order-service ./order-service
COPY profile-service ./profile-service
COPY reservation-service ./reservation-service

# Build argument to specify which service to build
ARG SERVICE_NAME

# Build the specified service
RUN gradle :${SERVICE_NAME}:bootJar -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

# Build argument (must be redeclared in each stage)
ARG SERVICE_NAME

# Create a non-root user
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy the built JAR from builder stage
COPY --from=builder /app/${SERVICE_NAME}/build/libs/*.jar app.jar

# Expose default port (can be overridden)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

