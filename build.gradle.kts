plugins {
    java
    application

    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2020.0.2")
    }
}

project.group = "app.oengus"
project.version = "2021.05.11"

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

application {
    mainClass.set("${project.group}.OengusApplication")
}

repositories {
    mavenCentral()
    jcenter()

    maven { url = uri("https://jitpack.io") }
}

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
    runtimeOnly(group = "org.postgresql", name = "postgresql")
    
    // JWT
    implementation(group = "io.jsonwebtoken", name = "jjwt", version = "0.9.1")
    implementation(group = "javax.xml.bind", name = "jaxb-api", version = "2.3.1")
    
    // APACHE
    implementation(group = "org.apache.commons", name = "commons-lang3", version = "3.9")
    implementation(group = "org.apache.commons", name = "commons-csv", version = "1.7")
    
    // FEIGN
    implementation(group = "org.springframework.cloud", name = "spring-cloud-starter-openfeign", version = "2.1.1.RELEASE")
    
    // JACKSON
    implementation(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-jsr310", version = "2.9.8")

    // HIBERNATE
    implementation(group = "com.vladmihalcea", name = "hibernate-types-52", version = "2.11.1")
    // implementation(group = "org.hibernate", name = "hibernate-ehcache", version = "5.3.9.Final")
    implementation(group = "org.hibernate", name = "hibernate-core", version = "5.4.31.Final")
    implementation(group = "org.hibernate", name = "hibernate-jcache", version = "5.4.31.Final")
    implementation(group = "org.ehcache", name = "ehcache", version = "3.8.1")


    // GUAVA (do we need this?)
    // implementation(group = "com.google.guava", name = "guava", version = "27.0.1-jre")

    // SWAGGER
    implementation(group = "io.springfox", name = "springfox-swagger2", version = "2.9.2")

    // iCal4J
    implementation(group = "org.mnode.ical4j", name = "ical4j", version = "3.0.11")

    // Paypal
    implementation(group = "com.paypal.sdk", name = "checkout-sdk", version = "1.0.3")

    // Twitter
    implementation(group = "org.twitter4j", name = "twitter4j-core", version = "4.0.7")

    // OKHTTP
    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = "3.14.9")

    // WEBHOOKS
    implementation(group = "com.github.esamarathon", name = "oengus-discord-webhooks", version = "b9bd477")
}

val wrapper: Wrapper by tasks

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.isIncremental = true
    options.compilerArgs = listOf("-Xlint:deprecation", "-Xlint:unchecked")
}

wrapper.apply {
    gradleVersion = "7.0.1"
    distributionType = Wrapper.DistributionType.ALL
}
