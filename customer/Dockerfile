FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy library module first (needed for dependencies)
COPY library/pom.xml /app/library/pom.xml
COPY library/src /app/library/src

# Copy customer module
COPY customer/pom.xml /app/customer/pom.xml
COPY customer/src /app/customer/src
COPY customer/.env /app/customer/.env

# Build library first (install to local Maven repo)
RUN cd /app/library && mvn clean install -DskipTests

# Build customer module
RUN cd /app/customer && mvn clean package -DskipTests

# Get the actual JAR filename
RUN mkdir -p /app/target-jar && \
    find /app/customer/target -name "*.jar" -not -name "*sources.jar" -not -name "*javadoc.jar" -not -name "*tests.jar" | xargs -I{} cp {} /app/target-jar/application.jar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target-jar/application.jar app.jar
COPY --from=build /app/customer/.env ./.env

# Add explicit healthcheck
HEALTHCHECK --interval=30s --timeout=3s CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]