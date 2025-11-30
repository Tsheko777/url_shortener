# Use OpenJDK 17 as base image
FROM eclipse-temurin:17-jdk-alpine

# Set work directory
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src src

# Grant execution to gradlew
RUN chmod +x ./gradlew

# Build the app
RUN ./gradlew clean build -x test

# Run the jar
ENTRYPOINT ["java", "-jar", "build/libs/url_shortener-0.0.1-SNAPSHOT.jar"]
