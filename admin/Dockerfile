FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app
# If you have a parent pom.xml at the root level
# COPY pom.xml ./pom.xml

# Copy and build library
WORKDIR /app/library
COPY library/pom.xml ./pom.xml
COPY library/src ./src

# Build library
WORKDIR /app
RUN mvn -B -f library/pom.xml clean install -DskipTests

# Build admin module
WORKDIR /app/admin
COPY admin/pom.xml ./pom.xml
COPY admin/src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/admin/target/*.jar admin.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "admin.jar"]