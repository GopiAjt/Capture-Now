FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar capture-now-docker.jar
ENTRYPOINT ["java", "-jar", "/capture-now-docker.jar"]
EXPOSE 8000