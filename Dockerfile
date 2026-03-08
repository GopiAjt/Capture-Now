# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY capturenow/ .
RUN mvn clean package -DskipTests

# Stage 2: Run the application - Use a slim JRE to save memory
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar capture-now-docker.jar

# JVM Flags for low-memory environments (Render Free Tier)
# -Xmx384m: Limits heap memory to 384MB, leaving room for non-heap/stack/OS.
# -Xss512k: Reduces thread stack size.
# -XX:+UseContainerSupport: Ensures Java respects Docker memory limits.
ENV JAVA_OPTS="-Xmx384m -Xms256m -Xss512k -XX:+UseContainerSupport"

ENV PORT=8080
EXPOSE $PORT

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar capture-now-docker.jar"]