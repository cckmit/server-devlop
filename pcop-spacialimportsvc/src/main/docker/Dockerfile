FROM openjdk:8-jdk-alpine
MAINTAINER Jimmy.Liu <liuzm@glodon.com>
VOLUME ["/tmp", "/data"]
ADD pcop-spacialimportsvc-0.0.3.jar /app.jar
EXPOSE 7114
ENV PCOPOPTS=""
ENTRYPOINT ["sh","-c","java -Djava.library.path=/usr/local/lib -Djava.security.egd=file:/dev/./urandom $PCOPOPTS -jar /app.jar"]
