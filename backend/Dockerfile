FROM openjdk:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/adapters/build/libs/adapters.jar /app/adapters.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/adapters.jar"]