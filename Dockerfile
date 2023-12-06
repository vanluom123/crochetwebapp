# Use the official Eclipse Temurin JDK 17 image with Alpine Linux
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Create a volume at /tmp
VOLUME /tmp

# Copy the JAR file into the container at /app
COPY build/libs/*.jar crochet.jar

# Expose port 8080 (adjust if your application uses a different port)
EXPOSE 8080

# Specify the command to run on container start
ENTRYPOINT ["java", "-jar", "/app/crochet.jar"]