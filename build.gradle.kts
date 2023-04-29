import org.apache.tools.ant.filters.ReplaceTokens

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
project.version = "2023.02.14"

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

    // COUNTRY SUPPORT
    implementation(group = "com.neovisionaries", name = "nv-i18n", version = "1.28")

    // Sentry
    implementation(group = "io.sentry", name = "sentry-spring-boot-starter", version = sentryVersion)
    implementation(group = "io.sentry", name = "sentry-logback", version = sentryVersion)

    // security and shit
    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.18.0")

    // idk
    implementation("org.javassist:javassist:3.29.1-GA")
    implementation("com.google.code.findbugs:jsr305:3.0.2")

    implementation(group = "com.rabbitmq", name = "amqp-client", version = "5.16.0")
}

val wrapper: Wrapper by tasks
val compileJava: JavaCompile by tasks

val sourcesForRelease = task<Copy>("sourcesForRelease") {
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

val generateJavaSources = task<SourceTask>("generateJavaSources") {
    val javaSources = sourceSets["main"].allJava.filter {
        !arrayOf("CoreConfiguration.java").contains(it.name)
    }.asFileTree

    source = javaSources + fileTree(sourcesForRelease.destinationDir)

    dependsOn(sourcesForRelease)
}

compileJava.apply {
    source = generateJavaSources.source

    dependsOn(generateJavaSources)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.isIncremental = true
    options.compilerArgs = listOf("-Xlint:deprecation", "-Xlint:unchecked")
}

wrapper.apply {
    gradleVersion = "7.5.1"
    distributionType = Wrapper.DistributionType.BIN
}
