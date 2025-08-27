# Use RHEL 8 with Oracle JDK 17 as base image
FROM ubi8/openjdk-17:latest

# Set working directory
WORKDIR /app

# Install necessary packages for Selenium
RUN dnf update -y && \
    dnf install -y \
    wget \
    curl \
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
    && dnf clean all

# Download and install Chrome
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | rpm --import - && \
    echo "[google-chrome]" > /etc/yum.repos.d/google-chrome.repo && \
    echo "name=google-chrome" >> /etc/yum.repos.d/google-chrome.repo && \
    echo "baseurl=http://dl.google.com/linux/chrome/rpm/stable/x86_64" >> /etc/yum.repos.d/google-chrome.repo && \
    echo "enabled=1" >> /etc/yum.repos.d/google-chrome.repo && \
    echo "gpgcheck=1" >> /etc/yum.repos.d/google-chrome.repo && \
    dnf install -y google-chrome-stable && \
    dnf clean all

# Copy Gradle files first for better caching
COPY gradle/ gradle/
COPY gradlew build.gradle gradle.properties ./

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src/ src/

# Build the application
RUN ./gradlew build -x test --no-daemon

# Create directories for scripts and screenshots
RUN mkdir -p /tmp/test-scripts /tmp/screenshots

# Expose port
EXPOSE 8080

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
CMD ["java", "-jar", "build/libs/selenium-execution-engine-0.0.1-SNAPSHOT.jar"]
