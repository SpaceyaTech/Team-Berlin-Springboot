# syntax=docker/dockerfile:1

FROM openjdk:17-slim

RUN mkdir /app
COPY . /app
WORKDIR /app

RUN ./mvnw dependency:resolve

CMD ["./mvnw", "spring-boot:run"]