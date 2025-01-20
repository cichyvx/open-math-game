FROM openjdk:21 as build

ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD Backend $HOME
RUN ./mvnw clean package

FROM openjdk:21 as runner
ARG JAR_FILE=/usr/app/target/*.jar
COPY --from=build $JAR_FILE /app/runner.jar
EXPOSE 8080
ENTRYPOINT java -jar /app/runner.jar