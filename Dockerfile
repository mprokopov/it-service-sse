FROM java:8-alpine
MAINTAINER Maksym Prokopov <mprokopov@gmail.com>

ADD target/it-service-sse-0.0.1-SNAPSHOT-standalone.jar /it-service-sse/app.jar

EXPOSE 9292

CMD ["java", "-jar", "/it-service-sse/app.jar"]
