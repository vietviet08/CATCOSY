FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app
COPY library/pom.xml .
COPY library/src ./src

# Ensure spring-boot-maven-plugin is properly configured
RUN mvn clean package -DskipTests

# Get the actual JAR filename
RUN mkdir -p /app/target-jar && \
    find /app/target -name "*.jar" -not -name "*sources.jar" -not -name "*javadoc.jar" -not -name "*tests.jar" | xargs -I{} cp {} /app/target-jar/application.jar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target-jar/application.jar app.jar

# Add explicit healthcheck
HEALTHCHECK --interval=30s --timeout=3s CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
# Use the Spring Boot specific command
ENTRYPOINT ["java", "-jar", "app.jar"]
