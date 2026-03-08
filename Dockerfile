# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
# Copy only the capturenow subdirectory source into the build container
COPY capturenow/ .
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar capture-now-docker.jar
ENV PORT=8080
EXPOSE $PORT
ENTRYPOINT ["java", "-jar", "capture-now-docker.jar"]