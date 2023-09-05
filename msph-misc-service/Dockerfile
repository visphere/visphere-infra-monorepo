FROM eclipse-temurin:17.0.4.1_1-jre AS builder

ARG OPEN_PORT

WORKDIR msph-misc-service/extracted

ADD target/*.jar executable.jar

RUN java -Djarmode=layertools -jar executable.jar extract

FROM eclipse-temurin:17.0.4.1_1-jre

COPY --from=builder msph-misc-service/extracted/dependencies/ ./
COPY --from=builder msph-misc-service/extracted/spring-boot-loader/ ./
COPY --from=builder msph-misc-service/extracted/snapshot-dependencies/ ./
COPY --from=builder msph-misc-service/extracted/application/ ./

COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

EXPOSE $OPEN_PORT
ENTRYPOINT [ "/usr/local/bin/entrypoint.sh" ]
