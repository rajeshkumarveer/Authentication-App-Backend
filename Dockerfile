# Use a lightweight Java image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy your Spring Boot JAR file
COPY target/AlNada-0.0.1-SNAPSHOT.jar app.jar

# Expose the port Spring Boot will run on
EXPOSE 8080

# Run the Spring Boot app
CMD ["java", "-jar", "app.jar"]
