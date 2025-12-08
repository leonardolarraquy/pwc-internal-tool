# Multi-stage build for frontend
FROM node:18 AS frontend-build

WORKDIR /app/frontend

COPY frontend/package*.json ./
RUN npm install

COPY frontend/ ./
RUN npm run build

# Build Spring Boot application
FROM maven:3.9-eclipse-temurin-17 AS backend-build

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Copy frontend build output
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static

RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Create data directory for H2 database persistence
RUN mkdir -p /app/data

COPY --from=backend-build /app/target/*.jar app.jar

EXPOSE 8080

# Use volume for data persistence
VOLUME ["/app/data"]

ENTRYPOINT ["java", "-jar", "app.jar"]

