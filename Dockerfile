FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace/app

# Arguments for the build
ARG ENV=dev
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
ARG ALLOWED_ORIGINS

# Environment variables
ENV ENV ${ENV}
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
ENV REDIS_HOST ${REDIS_HOST}
ENV REDIS_PORT ${REDIS_PORT}
ENV REDIS_PASSWORD ${REDIS_PASSWORD}
ENV SERVICE_ACCOUNT_KEY ${SERVICE_ACCOUNT_KEY}
ENV ALLOWED_ORIGINS ${ALLOWED_ORIGINS}

# Copy project files
COPY . /workspace/app

# Build project
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew clean build -x test \
    && mkdir -p build/dependency \
    && (cd build/dependency; jar -xf ../libs/*-SNAPSHOT.jar) \
    && rm -rf /root/.gradle

# Build image
FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Optimize JVM parameters
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-cp", "app:app/lib/*", \
    "org.crochet.CrochetApplication"]