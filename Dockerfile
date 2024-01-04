
# Use the official Oracle OpenJDK base image for Java 18
FROM openjdk:18-jdk


# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file into the container
COPY *.jar app.jar
CMD ["--server.port=8080"]
# Expose the port that the application will run on
EXPOSE 8080

# Start the Spring Boot application when the container starts
CMD ["java", "-jar", "app.jar"]
