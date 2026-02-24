plugins {
    kotlin("jvm") version "2.2.20"
    id("org.jmailen.kotlinter")
}

group = "no.nav.helsearbeidsgiver"
version = "0.1.0"

repositories {
    mavenCentral()
    maven {
        name = "VeraPDF Maven Repo"
        url = uri("https://software.verapdf.org/maven")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testImplementation("org.testcontainers:testcontainers:2.0.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
    testImplementation("io.ktor:ktor-client-core:3.3.3")
    testImplementation("io.ktor:ktor-client-apache5:3.3.3")
    testImplementation("org.slf4j:slf4j-simple:2.0.9")
    testImplementation("org.apache.pdfbox:pdfbox:3.0.1")
    // PDFBox implementation of veraPDF
    testImplementation("org.verapdf:validation-model:1.28.1")
    testImplementation("org.verapdf:verapdf-library:1.28.1")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("junit.jupiter.testclass.order.default", "org.junit.jupiter.api.ClassOrderer\$OrderAnnotation")
}

kotlin {
    jvmToolchain(21)
}
