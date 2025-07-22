FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar /app/ms-pe.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","ms-pe.jar"]