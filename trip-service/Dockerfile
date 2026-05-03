FROM gradle:8.14.4-jdk21-corretto AS build
WORKDIR /app

COPY . .

RUN gradle :api-gateway:bootJar -x test

FROM amazoncorretto:21
WORKDIR /app

COPY --from=build /app/api-gateway/build/libs/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
