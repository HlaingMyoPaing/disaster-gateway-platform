# ---------- BUILD STAGE ----------
FROM maven:3.9.8-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /src

# Use local Maven repo for caching dependencies
ENV MAVEN_OPTS="-Dmaven.repo.local=/root/.m2/repository"

# 1) Copy only pom.xml to cache dependencies
COPY api/pom.xml ./api/pom.xml

# Pre-download dependencies (offline mode)
RUN --mount=type=cache,target=/root/.m2 \
    mvn -f api/pom.xml -B -DskipTests dependency:go-offline

# 2) Copy source files and build the fat JAR
COPY api/ ./api/

RUN --mount=type=cache,target=/root/.m2 \
    mvn -f api/pom.xml -B -DskipTests package && \
    # copy the produced jar to a deterministic filename for the runtime stage
    cp api/target/*.jar api/api.jar


# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre

# Set environment variables
ENV TZ=Asia/Tokyo \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC"

# Create non-root user with a home directory
RUN addgroup --system app && adduser --system --ingroup app --home /home/app app

# Set working directory
WORKDIR /opt/app

# Copy built jar from build stage and set ownership in a single layer
COPY --from=build --chown=app:app /src/api/api.jar ./app.jar

# Create logs directory and ensure permissions (owned by app)
RUN mkdir -p /opt/app/logs && chown -R app:app /opt/app

# Switch to non-root user
USER app

# Expose application port
EXPOSE 8088

# Use sh -c + exec so JAVA_OPTS are expanded and java becomes PID 1 (signal forwarding)
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /opt/app/app.jar"]