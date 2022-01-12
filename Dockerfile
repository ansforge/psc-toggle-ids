FROM maven:3-jdk-11 AS build
COPY settings-docker.xml /usr/share/maven/ref/
COPY pom.xml /usr/src/app/pom.xml
COPY psc-api /usr/src/app/psc-api
COPY psc-toggle-manager /usr/src/app/psc-toggle-manager
RUN mvn -f /usr/src/app/pom.xml -gs /usr/share/maven/ref/settings-docker.xml clean package
FROM openjdk:11-slim-buster
COPY --from=build /usr/src/app/psc-toggle-manager/target/psc-toggle-manager-*.jar /usr/app/psc-toggle-manager.jar
RUN mkdir -p /app
RUN chown -R daemon: /app
USER daemon
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java -jar /usr/app/psc-toggle-manager.jar"]
