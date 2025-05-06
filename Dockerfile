# ---------- Build stage ----------
    FROM maven:3.9-eclipse-temurin-21-alpine AS build

    WORKDIR /app
    
    # Copy pom.xml and resolve dependencies (leverages Docker cache)
    COPY pom.xml .
    RUN mvn dependency:go-offline
    
    # Copy the source code
    COPY src ./src
    
    # Build the application with production profile
    RUN mvn package -DskipTests
    
    # ---------- Runtime stage ----------
    FROM eclipse-temurin:21-jre-alpine
    
    WORKDIR /app
    
    # Copy the built jar from the build stage
    COPY --from=build /app/target/*.jar app.jar
    
    # Ensure Spring Boot listens on the correct port in Cloud Run
    ENV PORT=8080
    EXPOSE 8080
    
    # Start the app
    ENTRYPOINT ["java", "-jar", "app.jar"]
    