FROM gradle:8.14.4-jdk21-corretto AS build
WORKDIR /app

COPY . .

RUN gradle :notification-service:bootJar -x test

FROM amazoncorretto:21
WORKDIR /app

COPY --from=build /app/notification-service/build/libs/*.jar app.jar

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
