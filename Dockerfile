FROM maven:3-jdk-8 as builder
COPY src/ src/
COPY pom.xml pom.xml
RUN mvn package -DskipTests -B

FROM fabric8/java-alpine-openjdk8-jre
RUN apk add --no-cache nss
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV AB_ENABLED=jmx_exporter
COPY --from=builder target/lib/* /deployments/lib/
COPY --from=builder target/*-runner.jar /deployments/app.jar
ENTRYPOINT [ "/deployments/run-java.sh" ]