FROM openjdk:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./gradlew -p datasource clean build -x test

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/datasource.jar /app/datasource.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/datasource.jar"]