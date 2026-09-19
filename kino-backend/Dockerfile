# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache Maven dependencies
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw -q dependency:go-offline

# Build the application
COPY src ./src
RUN ./mvnw -q -Dmaven.test.skip=true package

# Stage 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create a non-root user
RUN groupadd -r kino && useradd -r -g kino kino

COPY --from=build /app/target/*.jar app.jar
RUN chown -R kino:kino /app
USER kino

EXPOSE 8080
ENV JAVA_OPTS=""
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
