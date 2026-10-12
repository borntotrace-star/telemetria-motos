FROM eclipse-temurin:26-jre

WORKDIR /app

COPY app/build/libs/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]