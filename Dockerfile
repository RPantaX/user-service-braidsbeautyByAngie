# user-service/Dockerfile
FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY target/msvc-users-0.0.1-SNAPSHOT.jar user-service.jar
ENV PORT 8080
EXPOSE $PORT

ENTRYPOINT ["java","-jar","-Xmx1024M","-Dserver.port=${PORT}","app.jar"]
