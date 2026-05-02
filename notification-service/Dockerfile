FROM gradle:9.4.1-jdk21-corretto AS build
WORKDIR /app

COPY . .

RUN gradle :notification-service:bootJar -x test

FROM amazoncorretto:21
WORKDIR /app

COPY --from=build /app/notification-service/build/libs/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
