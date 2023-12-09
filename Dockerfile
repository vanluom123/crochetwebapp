## Use the official Eclipse Temurin JDK 17 image with Alpine Linux
#FROM eclipse-temurin:17-jdk-alpine
#
## Set the working directory inside the container
#WORKDIR /app
#
## Create a volume at /tmp
#VOLUME /tmp
#
## Copy the JAR file into the container at /app
#COPY build/libs/*.jar crochet.jar
#
## Expose port 8080 (adjust if your application uses a different port)
#EXPOSE 8080
#
## Specify the command to run on container start
#ENTRYPOINT ["java", "-jar", "/app/crochet.jar"]


# syntax=docker/dockerfile:experimental
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

COPY . /workspace/app
RUN --mount=type=cache,target=/root/.gradle
RUN chmod +x ./gradlew
RUN ./gradlew clean build
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","org.crochet.CrochetApplication"]