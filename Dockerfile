FROM eclipse-temurin:26-jre

WORKDIR /app

COPY --chown=10001:10001 app/build/libs/app.jar app.jar

USER 10001:10001

ENTRYPOINT ["java", "-jar", "app.jar"]
