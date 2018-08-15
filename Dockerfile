FROM openjdk:10-jre-slim

MAINTAINER Carlos Kobayashi <carlos.kobayashi@gmail.com>

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY target/checkout-api.jar $PROJECT_HOME/checkout-api.jar

WORKDIR $PROJECT_HOME

EXPOSE 8080
CMD ["java","-Dmongo.hostname=mongodb","-jar","./checkout-api.jar"]