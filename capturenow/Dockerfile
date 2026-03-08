FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar capture-now-docker.jar
ENV PORT=5000
EXPOSE $PORT
ENTRYPOINT ["java", "-jar", "/capture-now-docker.jar"]