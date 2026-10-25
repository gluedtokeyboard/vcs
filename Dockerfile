FROM eclipse-temurin:25-jre
WORKDIR /app
RUN groupadd -r java && useradd -r -g java java
COPY ./vcs-api/target/*.jar app.jar
USER java
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar", "app.jar"]
