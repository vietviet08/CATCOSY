FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy only the library module
COPY library/pom.xml ./pom.xml
COPY library/src ./src

# Install library to local Maven repo (don't try to run it)
RUN mvn clean install -DskipTests

# For verification only (remove in production)
RUN echo "Library JAR has been built and installed to Maven repository"

# Use a minimal container for the "runtime" - not actually running anything
FROM alpine:3.19
WORKDIR /app
COPY --from=build /root/.m2/repository/ /app/repository/

# Just keep container alive for dependency purposes
CMD ["sh", "-c", "echo 'Library module installed' && tail -f /dev/null"]
