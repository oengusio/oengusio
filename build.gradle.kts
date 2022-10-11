plugins {
    java
    application

    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.2")
    }
}

project.group = "app.oengus"
project.version = "2022.09.10"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass.set("${project.group}.OengusApplication")
}

repositories {
    mavenCentral()
    jcenter()

    maven { url = uri("https://jitpack.io") }
}

val sentryVersion = "5.7.0"

dependencies {
    // SPRING
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-validation")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-actuator")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-data-jpa")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-security")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-web") {
        // remove tomcat as we replace it with undertow
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-undertow")
    implementation(group = "org.liquibase", name = "liquibase-core")
    implementation(group = "io.micrometer", name = "micrometer-registry-prometheus")
    
    // POSTGRESQL
    implementation(group = "com.zaxxer", name = "HikariCP", version = "5.0.1")
    implementation(group = "org.postgresql", name = "postgresql")

    // JWT
    implementation(group = "io.jsonwebtoken", name = "jjwt", version = "0.9.1")
    implementation(group = "javax.xml.bind", name = "jaxb-api", version = "2.3.1")
    
    // APACHE
    implementation(group = "org.apache.commons", name = "commons-lang3", version = "3.9")
    implementation(group = "org.apache.commons", name = "commons-csv", version = "1.9.0")
    
    // FEIGN
    implementation(group = "org.springframework.cloud", name = "spring-cloud-starter-openfeign", version = "3.1.2")
    
    // JACKSON
    implementation(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-jsr310", version = "2.13.2")

    // documentation
    implementation(group = "org.springdoc", name = "springdoc-openapi-webmvc-core", version = "1.6.8")

    // iCal4J
    implementation(group = "org.mnode.ical4j", name = "ical4j", version = "3.2.2")

    // Paypal
    implementation(group = "com.paypal.sdk", name = "checkout-sdk", version = "1.0.3")

    // Twitter
    implementation(group = "org.twitter4j", name = "twitter4j-core", version = "4.0.7")

    // OKHTTP
    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = "3.14.9")

    // WEBHOOKS
    implementation(group = "com.github.esamarathon", name = "oengus-discord-webhooks", version = "b9bd477")

    // COUNTRY SUPPORT
    implementation(group = "com.neovisionaries", name = "nv-i18n", version = "1.28")

    // Sentry
    implementation(group = "io.sentry", name = "sentry-spring-boot-starter", version = sentryVersion)
    implementation(group = "io.sentry", name = "sentry-logback", version = sentryVersion)

    // security and shit
    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.18.0")

    // idk
    implementation("org.javassist:javassist:3.29.1-GA")

    implementation(group = "com.rabbitmq", name = "amqp-client", version = "5.16.0")
}

val wrapper: Wrapper by tasks

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.isIncremental = true
    options.compilerArgs = listOf("-Xlint:deprecation", "-Xlint:unchecked")
}

wrapper.apply {
    gradleVersion = "7.4.2"
    distributionType = Wrapper.DistributionType.ALL
}
