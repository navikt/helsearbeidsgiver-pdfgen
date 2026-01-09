import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22"
}

group = "no.nav.helse"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testImplementation("org.testcontainers:testcontainers:1.19.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
    testImplementation("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation("org.slf4j:slf4j-simple:2.0.9")
    testImplementation("org.apache.pdfbox:pdfbox:3.0.1")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("junit.jupiter.testclass.order.default", "org.junit.jupiter.api.ClassOrderer\$OrderAnnotation")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "21"
//        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

kotlin {
    jvmToolchain(21)
}
