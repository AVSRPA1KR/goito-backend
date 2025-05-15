FROM openjdk:24-jdk-alpine

WORKDIR /app

COPY target/*.jar /app/api.jar

ENTRYPOINT ["java", "-jar", "/app/api.jar"]