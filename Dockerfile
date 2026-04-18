# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application (skipping tests to speed up the process)
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime environment
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the generated jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (default Spring Boot port is 8080)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
