plugins {
    java
    application

    id("org.springframework.boot") version "2.4.13"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2020.0.5")
    }
}

project.group = "app.oengus"
project.version = "2022.04.23"

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
    implementation(group = "com.zaxxer", name = "HikariCP", version = "4.0.3")
    implementation(group = "org.postgresql", name = "postgresql")

    // JWT
    implementation(group = "io.jsonwebtoken", name = "jjwt", version = "0.9.1")
    implementation(group = "javax.xml.bind", name = "jaxb-api", version = "2.3.1")
    
    // APACHE
    implementation(group = "org.apache.commons", name = "commons-lang3", version = "3.9")
    implementation(group = "org.apache.commons", name = "commons-csv", version = "1.7")
    
    // FEIGN
    implementation(group = "org.springframework.cloud", name = "spring-cloud-starter-openfeign", version = "2.2.10.RELEASE")
    
    // JACKSON
    implementation(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-jsr310", version = "2.9.8")

    // HIBERNATE
    implementation(group = "com.vladmihalcea", name = "hibernate-types-52", version = "2.11.1")
    implementation(group = "org.hibernate", name = "hibernate-core", version = "5.4.31.Final")
    implementation(group = "org.hibernate", name = "hibernate-jcache", version = "5.4.31.Final")
    runtimeOnly(group = "org.ehcache", name = "ehcache", version = "3.8.1")

    // SWAGGER
     implementation(group = "io.springfox", name = "springfox-boot-starter", version = "3.0.0")
//    implementation(group = "io.springfox", name = "springfox-swagger2", version = "3.0.0")

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
    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.17.1")
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
