FROM ubuntu

RUN apt-get update
RUN apt-get install -y openjdk-8-jre-headless

COPY target/chairwise-input-service-0.0.1-SNAPSHOT.jar /home/app.jar
ENTRYPOINT ["java","-jar","/home/app.jar"]