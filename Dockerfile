# Use an official OpenJDK 22 runtime as a parent image
FROM arm64v8/openjdk:22-ea-1-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged jar file into the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
