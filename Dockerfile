FROM maven:3.9.10-eclipse-temurin-21 AS build
ARG MODULE
WORKDIR /workspace
COPY . .
RUN mvn -q -pl ${MODULE} -am -DskipTests package

FROM eclipse-temurin:21-jre-alpine
ARG MODULE
RUN addgroup -S eventra && adduser -S eventra -G eventra
WORKDIR /app
COPY --from=build /workspace/${MODULE}/target/${MODULE}-*.jar app.jar
USER eventra
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
