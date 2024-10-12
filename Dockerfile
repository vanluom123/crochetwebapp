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
ARG REDIS_HOST
ARG REDIS_PORT
ARG REDIS_PASSWORD
ARG SERVICE_ACCOUNT_KEY

# Environment variables
ENV env=${env} \
    EMAIL=${EMAIL} \
    DB_HOST=${DB_HOST} \
    DB_PORT=${DB_PORT} \
    DB_NAME=${DB_NAME} \
    DB_USERNAME=${DB_USERNAME} \
    DB_PASSWORD=${DB_PASSWORD} \
    GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID} \
    GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET} \
    TOKEN_SECRET=${TOKEN_SECRET} \
    APP_PASSWORD=${APP_PASSWORD} \
    PAYPAL_CLIENT_ID=${PAYPAL_CLIENT_ID} \
    PAYPAL_CLIENT_SECRET=${PAYPAL_CLIENT_SECRET} \
    REDIS_HOST=${REDIS_HOST} \
    REDIS_PORT=${REDIS_PORT} \
    REDIS_PASSWORD=${REDIS_PASSWORD} \
    SERVICE_ACCOUNT_KEY=${SERVICE_ACCOUNT_KEY}

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