FROM ubuntu:latest
LABEL authors="petertogara"

# STEP 1: Build Stage
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

# STEP 2: Run Stage
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8888
EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]