# Use RHEL 8 with Oracle JDK as base image
FROM registry.access.redhat.com/ubi8/openjdk-17:latest

# Set working directory
WORKDIR /app

# Install required packages for Selenium
RUN microdnf update -y && \
    microdnf install -y \
    wget \
    unzip \
    xvfb \
    libXcomposite \
    libXcursor \
    libXdamage \
    libXext \
    libXi \
    libXtst \
    cups-libs \
    libXScrnSaver \
    libXrandr \
    alsa-lib \
    pango \
    atk \
    at-spi2-atk \
    gtk3 \
    nss \
    && microdnf clean all

# Copy Gradle wrapper files
COPY gradlew .
COPY gradle gradle
COPY gradle.properties gradle.properties

# Copy source code
COPY src src
COPY build.gradle .

# Make gradlew executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew build -x test

# Create directory for screenshots
RUN mkdir -p /tmp/screenshots

# Expose port
EXPOSE 8080

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
CMD ["java", "-jar", "build/libs/selenium-execution-engine-0.0.1-SNAPSHOT.jar"]
