FROM openjdk:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/moex-datasource.jar /app/moex-datasource.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/moex-datasource.jar"]