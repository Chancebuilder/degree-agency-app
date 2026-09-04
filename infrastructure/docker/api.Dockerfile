FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY apps/api/pom.xml apps/api/pom.xml
WORKDIR /src/apps/api
RUN mvn -q -DskipTests dependency:go-offline
COPY apps/api /src/apps/api
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/apps/api/target/degree-dean-api-0.1.0.jar app.jar
EXPOSE 18428
ENTRYPOINT ["java", "-jar", "app.jar"]
