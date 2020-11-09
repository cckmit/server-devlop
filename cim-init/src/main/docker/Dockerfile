FROM openjdk:8-jdk-alpine
MAINTAINER Jimmy.Liu <liuzm@glodon.com>
VOLUME ["/tmp", "/data"]
ADD cim-init-0.0.3.jar /app.jar
EXPOSE 7333
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8
ENV PCOPOPTS=""
ENTRYPOINT ["sh","-c","java -Djava.security.egd=file:/dev/./urandom $PCOPOPTS -jar /app.jar"]
