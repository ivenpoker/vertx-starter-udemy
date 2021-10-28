# Docker example using fatjar
# - docker build -t example/vertx-starter .
# - docker run -it -p 9999:9999 example/vertx-starter --name vertx-starter-container

FROM adoptopenjdk:15-jre-hotspot

ENV FAT_JAR vertx-starter-1.0.0-SNAPSHOT-fat.jar
ENV APP_HOME /usr/app

EXPOSE 9999

COPY build/libs/$FAT_JAR $APP_HOME/

WORKDIR $APP_HOME

ENTRYPOINT ["sh", "-c"]

CMD ["exec java -jar $FAT_JAR"]
