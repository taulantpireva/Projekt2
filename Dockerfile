# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pirevtau/pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the application code and build it
COPY pirevtau/src ./src
COPY models ./models 
RUN mvn clean package -DskipTests

# Stage 2: Run stage
FROM eclipse-temurin:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Copy the trained model from the build stage
COPY --from=build /app/models /app/models

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
