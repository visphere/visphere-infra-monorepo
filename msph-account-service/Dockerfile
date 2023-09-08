FROM eclipse-temurin:17.0.4.1_1-jre AS builder

WORKDIR msph-account-service/extracted

ADD target/*.jar executable.jar

RUN java -Djarmode=layertools -jar executable.jar extract

FROM eclipse-temurin:17.0.4.1_1-jre

WORKDIR app

COPY --from=builder msph-account-service/extracted/dependencies/ ./
COPY --from=builder msph-account-service/extracted/spring-boot-loader/ ./
COPY --from=builder msph-account-service/extracted/snapshot-dependencies/ ./
COPY --from=builder msph-account-service/extracted/application/ ./

COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

EXPOSE 8080
ENTRYPOINT [ "/usr/local/bin/entrypoint.sh" ]
