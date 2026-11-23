import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.kotlin.dsl.register

plugins {
    java
    application

    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.freefair.lombok") version "9.5.0"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.1.4")
    }
}

project.group = "app.oengus"
// Version code is year.month.(release num)
project.version = "2026.03.1"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

application {
    mainClass.set("${project.group}.OengusApplication")
}

repositories {
    mavenCentral()
}

val snippetsDir = file("build/generated-snippets")

extra["snippetsDir"] = snippetsDir

val sentryVersion = "8.64.0"
val mapstructVersion = "1.6.3"

// TODO: gradle version catalog
dependencies {
    // SPRING
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
//    implementation("org.liquibase:liquibase-core")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Authentication + OTP
    implementation("com.google.zxing:core:3.5.4")
    implementation("de.taimos:totp:1.0")

    // POSTGRESQL
    implementation("org.postgresql:postgresql:42.7.14")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

    // APACHE
    implementation("org.apache.commons:commons-lang3:3.20.0")
    implementation("org.apache.commons:commons-csv:1.14.1")

    // FEIGN
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // JACKSON
    implementation("tools.jackson.core:jackson-core:3.3.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.23")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.23.0")

    // documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:3.1.0")

    // iCal4J
    implementation("org.mnode.ical4j:ical4j:4.3.0")

    // Paypal
    implementation("com.paypal.sdk:checkout-sdk:2.0.0")

    // OKHTTP
    implementation("com.squareup.okhttp3:okhttp:5.4.0")

    // COUNTRY SUPPORT
    implementation("com.neovisionaries:nv-i18n:1.29")

    // Sentry
    implementation("io.sentry:sentry-spring-boot-4:$sentryVersion")
    implementation("io.sentry:sentry-logback:$sentryVersion")

    // security and shit
    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.26.1")
    implementation("org.passay:passay:2.0.0")

    // idk
    implementation("org.javassist:javassist:3.32.0-GA")
    implementation("com.google.code.findbugs:jsr305:3.0.2")

    implementation("com.rabbitmq:amqp-client:5.37.1")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")

    // development tools, will be removed in production builds
//    developmentOnly("org.springframework.boot:spring-boot-devtools")

//    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

    // And of course, we are going to write unit tests.
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-liquibase-test")
    testImplementation("org.springframework.boot:spring-boot-starter-mail-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-cache-test")
    testImplementation("net.datafaker:datafaker:2.7.0")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    testRuntimeOnly("com.h2database:h2:2.4.240")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val wrapper: Wrapper by tasks
val compileJava: JavaCompile by tasks

val sourcesForRelease = tasks.register<Copy>("sourcesForRelease") {
    from("src/main/java") {
        include("**/CoreConfiguration.java")

        val items = mapOf(
            "OENGUS_VERSION" to project.version
        )

        filter<ReplaceTokens>(mapOf("tokens" to items))
    }

    into("build/filteredSrc")

    includeEmptyDirs = false
}

val generateJavaSources = tasks.register<SourceTask>("generateJavaSources") {
    val javaSources = sourceSets["main"].allJava.filter {
        !arrayOf("CoreConfiguration.java").contains(it.name)
    }.asFileTree

    source = javaSources + fileTree(sourcesForRelease.get().destinationDir)

    dependsOn(sourcesForRelease)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    outputs.dir(snippetsDir)

    testLogging {
        showStandardStreams = System.getenv("CI") == null
        exceptionFormat = TestExceptionFormat.FULL
    }
}

compileJava.apply {
    source = generateJavaSources.get().source

    dependsOn(generateJavaSources)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.isIncremental = true
    // NOTE: use -java-parameters for the kotlin compiler
    // see https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-6.1-Release-Notes
    options.compilerArgs = listOf("-Xlint:deprecation", "-Xlint:unchecked", "-parameters")
}

wrapper.apply {
    gradleVersion = "9.3.1"
    distributionType = Wrapper.DistributionType.BIN
}
