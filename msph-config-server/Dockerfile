FROM eclipse-temurin:17.0.4.1_1-jre AS builder

WORKDIR msph-config-server/extracted

ADD target/*.jar executable.jar

RUN java -Djarmode=layertools -jar executable.jar extract

FROM eclipse-temurin:17.0.4.1_1-jre

WORKDIR app

COPY --from=builder msph-config-server/extracted/dependencies/ ./
COPY --from=builder msph-config-server/extracted/spring-boot-loader/ ./
COPY --from=builder msph-config-server/extracted/snapshot-dependencies/ ./
COPY --from=builder msph-config-server/extracted/application/ ./

COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

EXPOSE 8888
ENTRYPOINT [ "/usr/local/bin/entrypoint.sh" ]

