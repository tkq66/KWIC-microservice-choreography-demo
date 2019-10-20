# Cache dependencies
FROM maven:3.6.2-jdk-8 as build
WORKDIR /home/app
COPY pom.xml .
RUN mvn dependency:go-offline

# Build the current project
COPY src ./src
RUN mvn package -DskipTests --activate-profiles dev-docker

# Host the application
FROM openjdk:8
RUN mkdir /opt/files
COPY --from=build /home/app/target/kwic-orchestration-service-0.0.1-SNAPSHOT.jar /usr/local/lib/app.jar
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]