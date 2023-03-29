FROM amazoncorretto:17

WORKDIR /app

COPY target/backend-0.0.1-SNAPSHOT.jar backend-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","/app/backend-0.0.1-SNAPSHOT.jar"]
