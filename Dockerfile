FROM openjdk:12.0.2 AS builder

WORKDIR /oengus
COPY gradle gradlew build.gradle.kts settings.gradle.kts ./
RUN ./gradlew dependencies
COPY . .
RUN ./gradlew build

FROM openjdk:12.0.2

COPY --from=builder build/libs/oengusio-all.jar .

CMD ["java", "-Dspring.profiles.active=${env.SPRING_PROFILE}", "-Xmx${env.JAVA_XMX}", "-jar", "oengus-all.jar"]