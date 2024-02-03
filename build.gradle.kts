plugins {
    application
    kotlin("jvm") version "1.9.22"
}

group = "fr.imacaron"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.kord:kord-core:0.13.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("org.codehaus.janino:janino:3.1.8")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

application {
    mainClass.set("fr.imacaron.kaamelott.gif.MainKt")
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources")) // We need this for Gradle optimization to work
        archiveClassifier.set("standalone") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
        archiveFileName = "kaamelott-gif.jar"
    }
    build {
        dependsOn(fatJar) // Trigger fat jar creation during build
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}