plugins {
    kotlin("jvm") version "1.9.22"
}

group = "fr.imacaron"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.kord:kord-core:0.13.1")
    implementation("org.slf4j:slf4j-simple:2.0.11")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")


    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}