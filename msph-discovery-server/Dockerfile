FROM eclipse-temurin:17.0.4.1_1-jre AS builder

WORKDIR msph-discovery-server/extracted

ADD target/*.jar executable.jar

RUN java -Djarmode=layertools -jar executable.jar extract

WORKDIR app

FROM eclipse-temurin:17.0.4.1_1-jre

COPY --from=builder msph-discovery-server/extracted/dependencies/ ./
COPY --from=builder msph-discovery-server/extracted/spring-boot-loader/ ./
COPY --from=builder msph-discovery-server/extracted/snapshot-dependencies/ ./
COPY --from=builder msph-discovery-server/extracted/application/ ./

COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

EXPOSE 8761
ENTRYPOINT [ "/usr/local/bin/entrypoint.sh" ]
