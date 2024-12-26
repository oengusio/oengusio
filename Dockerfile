FROM azul/zulu-openjdk-alpine:21 AS builder

WORKDIR /oengus-backend
COPY gradle ./gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
RUN ./gradlew --no-daemon dependencies
COPY . .
RUN ./gradlew --no-daemon bootJar

FROM azul/zulu-openjdk-alpine:21-jre

WORKDIR /oengus-backend
COPY --from=builder /oengus-backend/build/libs/oengusio-*.jar ./oengusio.jar
COPY entrypoint.sh ./
RUN chmod +x entrypoint.sh
CMD ["./entrypoint.sh"]
