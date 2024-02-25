plugins {
    kotlin("jvm") version "1.9.22"
}

group = "fr.imacaron"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("ch.qos.logback:logback-core:1.4.14")
    implementation("org.ktorm:ktorm-core:3.6.0")
    implementation("org.ktorm:ktorm-support-mysql:3.6.0")
    implementation("com.mchange:c3p0:0.9.5.5")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}