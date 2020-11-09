#FROM openjdk:8-jdk-alpine
#FROM packages.glodon.com/docker-pcop-public/pcop/cim/pcop-libreoffice:0.1
FROM packages.glodon.com/docker-pcop-public/pcop/cim/pcop-openoffice:0.1
MAINTAINER Jimmy.Liu <liuzm@glodon.com>
VOLUME ["/tmp", "/data"]
ADD pcop-cimsvc-0.0.3.jar /app.jar
EXPOSE 7109
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8
ENV PCOPOPTS=""
ENTRYPOINT ["sh","-c","java -Djava.security.egd=file:/dev/./urandom $PCOPOPTS -jar /app.jar"]
