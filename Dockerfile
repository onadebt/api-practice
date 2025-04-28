FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn -B package

FROM eclipse-temurin:17-jre-alpine

ARG JAR_FILE=/app/target/rest-service-1.0.0.jar
COPY --from=build ${JAR_FILE} /app/rest-service.jar

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/rest-service.jar"]
