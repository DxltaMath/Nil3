import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "DxltaMath"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

repositories {
    maven("https://repo.zugazagoitia.com/snapshots")
}

dependencies {
    implementation("io.javalin", "javalin-ssl", "5.0.0-SNAPSHOT")
    implementation("io.javalin", "javalin", "5.0.0.RC2")
    implementation("org.slf4j", "slf4j-simple", "2.0.1")
    implementation("org.junit.jupiter:junit-jupiter:5.9.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.7.10")
    implementation(kotlin("script-runtime"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}

application {
    mainClass.set("MainKt")
}
