FROM maven:3-jdk-11 as builder
COPY pom.xml pom.xml
COPY src/ src/
RUN mvn package -DskipTests -B

FROM openjdk:11.0.4-jre-slim
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
COPY --from=builder target/lib/* /deployments/lib/
COPY --from=builder target/*-runner.jar /deployments/app.jar
ENTRYPOINT [ "java", "-jar",  "/deployments/app.jar"]