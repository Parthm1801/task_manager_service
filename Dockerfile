# Use an official Java 21 runtime image
FROM eclipse-temurin:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the application's JAR file
COPY target/task_manager_service-0.0.1-SNAPSHOT.jar app.jar

# Expose the port that your Spring Boot app runs on
EXPOSE 8080

# Use environment variables for sensitive data
ENV DATASOURCE_URL=${DATASOURCE_URL}
ENV DATASOURCE_USERNAME=${DATASOURCE_USERNAME}
ENV DATASOURCE_PASSWORD=${DATASOURCE_PASSWORD}
ENV JWT_SECRET_KEY=${JWT_SECRET_KEY}

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
