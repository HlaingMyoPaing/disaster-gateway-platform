# ---------- BUILD STAGE ----------
FROM maven:3.9.8-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /src

# Use local Maven repo for caching dependencies
ENV MAVEN_OPTS="-Dmaven.repo.local=/root/.m2/repository"

# 1) Copy only pom.xml to cache dependencies
COPY app/pom.xml ./app/pom.xml

# Pre-download dependencies (offline mode)
RUN --mount=type=cache,target=/root/.m2 \
    mvn -f app/pom.xml -B -DskipTests dependency:go-offline

# 2) Copy source files and build the fat JAR
COPY app/ ./app/

RUN --mount=type=cache,target=/root/.m2 \
    mvn -f app/pom.xml -B -DskipTests package


# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre

# Set environment variables
ENV TZ=Asia/Tokyo \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC"

# Create non-root user
RUN addgroup --system app && adduser --system --ingroup app app

# Set working directory
WORKDIR /opt/app

# Copy built jar from build stage
COPY --from=build /src/app/target/*.jar ./app.jar

# Create logs directory and fix permissions
RUN mkdir -p logs && chown -R app:app .

# Switch to non-root user
USER app

# Expose application port
EXPOSE 8088

# Use exec form to ensure proper signal handling (e.g., SIGTERM)
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD []
