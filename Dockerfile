FROM java:8-alpine
MAINTAINER Maksym Prokopov <mprokopov@gmail.com>

ENV APP it-service-sse-1.3.0-standalone.jar
ADD target/$APP /it-service-sse/app.jar

EXPOSE 9292

CMD ["java", "-jar", "/it-service-sse/app.jar"]
