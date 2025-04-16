# Use an official Maven image to build the Spring Boot app
FROM maven:3.8.4-openjdk-17 AS build

# Set working directory inside the container
WORKDIR /app

# Fix: Add destination path to COPY command
COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app

# Fix: Add destination filename
COPY --from=build /app/target/group-project-0.0.1-SNAPSHOT.jar .

# Expose the port that the Spring Boot app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/group-project-0.0.1-SNAPSHOT.jar"]