FROM maven:3.9.9-sapmachine-22 AS build
LABEL authors="yakov nadler"
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Use OpenJDK image to run the application
FROM openjdk:22-jdk-slim
COPY --from=build /app/target/player-service-0.0.1-SNAPSHOT.jar /app/player-service.jar
ENTRYPOINT ["java", "-jar", "/app/player-service.jar"]
