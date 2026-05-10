# syntax=docker/dockerfile:1.7

# ── build stage ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy poms first so Maven can cache dependency resolution if sources change.
COPY pom.xml ./
COPY email-client/pom.xml email-client/
COPY auth-server/pom.xml  auth-server/
COPY app/pom.xml          app/

RUN mvn -B -q -pl app -am dependency:go-offline -DskipTests || true

# Now copy sources and build the app module (with dependencies).
COPY email-client/src email-client/src
COPY auth-server/src  auth-server/src
COPY app/src          app/src

RUN mvn -B -pl app -am package -DskipTests

# ── runtime stage ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/app/target/app-*-exec.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
