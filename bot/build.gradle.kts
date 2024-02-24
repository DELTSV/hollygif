plugins {
    application
    kotlin("jvm") version "1.9.22"
    id("org.liquibase.gradle") version "2.2.1"
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
    implementation("ch.qos.logback:logback-core:1.4.14")
    implementation("org.codehaus.janino:janino:3.1.8")
    implementation("org.liquibase:liquibase-core:4.25.1")
    implementation("org.ktorm:ktorm-core:3.6.0")
    implementation("org.ktorm:ktorm-support-mysql:3.6.0")
    implementation("com.mchange:c3p0:0.9.5.5")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.3.2")

    liquibaseRuntime("org.liquibase:liquibase-core:4.25.1")
    liquibaseRuntime("org.mariadb.jdbc:mariadb-java-client:3.3.2")
    liquibaseRuntime("info.picocli:picocli:4.7.5")
    liquibaseRuntime("org.yaml:snakeyaml:2.2")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

application {
    mainClass.set("fr.imacaron.kaamelott.gif.MainKt")
}

liquibase {
    activities.register("main") {
        val dbUrl = System.getenv("DB_URL")
        val dbUser = System.getenv("DB_USER")
        val dbPass = System.getenv("DB_PASSWORD")
        val refDbUrl = System.getenv("REF_DB_URL")
        val refDbUser = System.getenv("REF_DB_USER")
        val refDbPass = System.getenv("REF_DB_PASSWORD")
        arguments = mapOf(
            "referenceUrl" to refDbUrl,
            "referenceUsername" to refDbUser,
            "referencePassword" to refDbPass,
            "logLevel" to "info",
            "changelogFile" to "bot/src/main/resources/migrations/changelog.mariadb.sql",
            "url" to dbUrl,
            "username" to dbUser,
            "password" to dbPass
        )
    }
    runList = "main"
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources")) // We need this for Gradle optimization to work
        archiveClassifier.set("standalone") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
        exclude("META-INF/*.RSA")
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
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