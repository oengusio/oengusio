FROM openjdk:12.0.2 AS builder

WORKDIR /oengus-backend
COPY gradle/ ./gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
RUN ./gradlew dependencies
COPY . .
RUN ./gradlew build

FROM openjdk:12.0.2

WORKDIR /oengus-backend
COPY --from=builder build/libs/oengusio-all.jar .

CMD ["java", "-Dspring.profiles.active=$SPRING_PROFILE", "-Xmx$JAVA_XMX", "-jar", "oengus-all.jar"]