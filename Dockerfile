FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

# Arguments for the build
ARG env=dev
ARG EMAIL
ARG DB_HOST
ARG DB_PORT
ARG DB_NAME
ARG DB_USERNAME
ARG DB_PASSWORD
ARG GOOGLE_CLIENT_ID
ARG GOOGLE_CLIENT_SECRET
ARG TOKEN_SECRET
ARG APP_PASSWORD
ARG PAYPAL_CLIENT_ID
ARG PAYPAL_CLIENT_SECRET

# Environment variables
ENV env ${env}
ENV EMAIL ${EMAIL}
ENV DB_HOST ${DB_HOST}
ENV DB_PORT ${DB_PORT}
ENV DB_NAME ${DB_NAME}
ENV DB_USERNAME ${DB_USERNAME}
ENV DB_PASSWORD ${DB_PASSWORD}
ENV GOOGLE_CLIENT_ID ${GOOGLE_CLIENT_ID}
ENV GOOGLE_CLIENT_SECRET ${GOOGLE_CLIENT_SECRET}
ENV TOKEN_SECRET ${TOKEN_SECRET}
ENV APP_PASSWORD ${APP_PASSWORD}
ENV PAYPAL_CLIENT_ID ${PAYPAL_CLIENT_ID}
ENV PAYPAL_CLIENT_SECRET ${PAYPAL_CLIENT_SECRET}

COPY . /workspace/app
RUN --mount=type=cache,target=/root/.gradle
RUN chmod +x ./gradlew
RUN ./gradlew preReleaseVersion
RUN ./gradlew majorVersionUpdate
RUN ./gradlew clean build
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","org.crochet.CrochetApplication"]