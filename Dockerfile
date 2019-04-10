FROM openjdk:8-jdk-alpine
#FROM maven:alpine


WORKDIR /app

COPY . /app

#RUN mvn -v
#RUN mvn clean install -DskipTests

EXPOSE 8884

LABEL maintainer='nick.christidis@yahoo.com'

ADD ./target/eresearch-dblp-consumer-1.0-boot.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["java", "-Dspring.profiles.active=container", "-jar", "app.jar", "container-entrypoint"]
