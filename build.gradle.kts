import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
    application

    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.asciidoctor.jvm.convert") version "4.0.4"
    id("io.freefair.lombok") version "8.11"
}

//dependencyManagement {
//    imports {
//        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.2")
//    }
//}

project.group = "app.oengus"
project.version = "2024.10.27"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
    jcenter()

    maven { url = uri("https://jitpack.io") }
}

val snippetsDir = file("build/generated-snippets")

extra["snippetsDir"] = snippetsDir

val sentryVersion = "7.9.0"
val mapstructVersion = "1.6.3"
val jacksonVersion = "2.18.2"

// TODO: gradle version catalog
dependencies {
    // SPRING
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-validation")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-actuator")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-data-jpa")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-security")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-mail")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-thymeleaf")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-web") {
        // remove tomcat as we replace it with undertow
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-undertow")
    implementation(group = "org.liquibase", name = "liquibase-core")
    implementation(group = "io.micrometer", name = "micrometer-registry-prometheus")

    // Authentication + OTP
    implementation(group = "com.google.zxing", name = "core", version = "3.5.3")
    implementation(group = "de.taimos", name = "totp", version = "1.0")

    // POSTGRESQL
    implementation(group = "com.zaxxer", name = "HikariCP", version = "6.2.1")
    implementation(group = "org.postgresql", name = "postgresql")

    // JWT
    implementation(group = "io.jsonwebtoken", name = "jjwt-api", version = "0.12.6")
    runtimeOnly(group = "io.jsonwebtoken", name = "jjwt-impl", version = "0.12.3")
    runtimeOnly(group = "io.jsonwebtoken", name = "jjwt-jackson", version = "0.12.6")
    // implementation(group = "javax.xml.bind", name = "jaxb-api", version = "2.3.1")

    // APACHE
    implementation(group = "org.apache.commons", name = "commons-lang3", version = "3.17.0")
    implementation(group = "org.apache.commons", name = "commons-csv", version = "1.9.0")

    // FEIGN
    implementation(group = "org.springframework.cloud", name = "spring-cloud-starter-openfeign", version = "3.1.9")

    // JACKSON
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-core", version = jacksonVersion)
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-annotations", version = jacksonVersion)
    implementation(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-jsr310", version = jacksonVersion)

    // documentation
    implementation(group = "org.springdoc", name = "springdoc-openapi-webmvc-core", version = "1.8.0")

    // iCal4J
    implementation(group = "org.mnode.ical4j", name = "ical4j", version = "4.0.7")

    // Paypal
    implementation(group = "com.paypal.sdk", name = "checkout-sdk", version = "1.0.3")

    // OKHTTP
    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = "4.12.0")

    // COUNTRY SUPPORT
    implementation(group = "com.neovisionaries", name = "nv-i18n", version = "1.29")

    // Sentry
    implementation(group = "io.sentry", name = "sentry-spring-boot-starter", version = sentryVersion)
    implementation(group = "io.sentry", name = "sentry-logback", version = sentryVersion)

    // security and shit
    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.24.3")
    implementation("org.passay:passay:1.6.6")

    // idk
    implementation("org.javassist:javassist:3.30.2-GA")
    implementation("com.google.code.findbugs:jsr305:3.0.2")

    implementation(group = "com.rabbitmq", name = "amqp-client", version = "5.24.0")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")

    // development tools, will be removed in production builds
//    developmentOnly("org.springframework.boot:spring-boot-devtools")

//    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

    // And of course, we are going to write unit tests.
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("net.datafaker:datafaker:2.4.2")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    testRuntimeOnly("com.h2database:h2")
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

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    outputs.dir(snippetsDir)

    testLogging {
        showStandardStreams = System.getenv("CI") == null
    }
}

tasks.asciidoctor {
    inputs.dir(snippetsDir)
    dependsOn(tasks.test)
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
    gradleVersion = "8.12"
    distributionType = Wrapper.DistributionType.BIN
}
