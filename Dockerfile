FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/Team20-0.0.1-SNAPSHOT.jar Team20.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/Team20.jar"]