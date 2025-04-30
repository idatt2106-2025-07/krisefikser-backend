# Dockerfile
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy pom.xml separately to leverage Docker cache for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# Runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Spring Boot runs on port 8080 by default
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]