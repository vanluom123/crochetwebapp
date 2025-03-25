# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace/app

# Copy chỉ các file cần thiết cho build
COPY gradlew .
COPY gradle gradle
COPY version.properties .
COPY version.gradle .
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Arguments for the build
ARG ENV
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
ARG SERVICE_ACCOUNT_KEY
ARG ALLOWED_ORIGINS

# Environment variables
ENV ENV ${ENV} \
    EMAIL ${EMAIL} \
    DB_HOST ${DB_HOST} \
    DB_PORT ${DB_PORT} \
    DB_NAME ${DB_NAME} \
    DB_USERNAME ${DB_USERNAME} \
    DB_PASSWORD ${DB_PASSWORD} \
    GOOGLE_CLIENT_ID ${GOOGLE_CLIENT_ID} \
    GOOGLE_CLIENT_SECRET ${GOOGLE_CLIENT_SECRET} \
    TOKEN_SECRET ${TOKEN_SECRET} \
    APP_PASSWORD ${APP_PASSWORD} \
    SERVICE_ACCOUNT_KEY ${SERVICE_ACCOUNT_KEY} \
    ALLOWED_ORIGINS ${ALLOWED_ORIGINS}

# Build project
RUN --mount=type=cache,target=/root/.gradle \
    chmod +x gradlew \
    && ./gradlew clean bootJar --no-daemon -x test \
    && mkdir -p build/dependency \
    && (cd build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp

ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# JVM tối ưu cho container
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-cp", "app:app/lib/*", \
    "org.crochet.CrochetApplication"]