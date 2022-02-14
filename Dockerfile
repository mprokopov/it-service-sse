FROM java:8-alpine
MAINTAINER Maksym Prokopov <mprokopov@gmail.com>

ENV APP it-service-sse-1.3.0-standalone.jar
ADD target/$APP /it-service-sse/app.jar

COPY config/logback.prod.xml /logback.xml

EXPOSE 9292

CMD ["java", "-Dlogback.configurationFile=/logback.xml", "-jar", "/it-service-sse/app.jar"]
