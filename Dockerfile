FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace

COPY pom.xml .

RUN mvn --batch-mode -DskipTests dependency:go-offline

COPY src ./src

RUN mvn --batch-mode -DskipTests package


FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S fleetops && adduser -S fleetops -G fleetops

COPY --from=build /workspace/target/fleetops-0.0.1-SNAPSHOT.jar /app/fleetops.jar

USER fleetops

EXPOSE 18081

ENTRYPOINT ["java", "-jar", "/app/fleetops.jar"]