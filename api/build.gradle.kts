val logback_version: String by project

plugins {
	application
	kotlin("jvm")
	id("io.ktor.plugin") version "3.1.1"
	id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
}

group = "fr.imacaron.gif"
version = "0.0.1"

application {
	mainClass.set("io.ktor.server.cio.EngineMain")

	val isDevelopment: Boolean = project.ext.has("development")
	applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("dev.kord:kord-core:0.15.0")
	implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
	implementation("io.ktor:ktor-server-core-jvm")
	implementation("io.ktor:ktor-server-resources")
	implementation("io.ktor:ktor-server-host-common-jvm")
	implementation("io.ktor:ktor-server-status-pages-jvm")
	implementation("io.ktor:ktor-server-cors-jvm")
	implementation("io.ktor:ktor-server-forwarded-header-jvm")
	implementation("io.ktor:ktor-server-openapi")
	implementation("io.ktor:ktor-server-swagger-jvm")
	implementation("io.ktor:ktor-server-call-logging-jvm")
	implementation("io.ktor:ktor-server-content-negotiation-jvm")
	implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
	implementation("io.ktor:ktor-server-cio-jvm")
	implementation("io.ktor:ktor-server-auth")
	implementation("io.ktor:ktor-client-core")
	implementation("io.ktor:ktor-client-cio")
	implementation("io.ktor:ktor-client-auth")
	implementation("io.ktor:ktor-server-config-yaml")
	implementation("ch.qos.logback:logback-classic:$logback_version")
	implementation("ch.qos.logback:logback-core:$logback_version")
	implementation("org.codehaus.janino:janino:3.1.8")
	implementation("org.ktorm:ktorm-core:3.6.0")
	implementation("org.ktorm:ktorm-support-mysql:3.6.0")
	implementation("com.mchange:c3p0:0.9.5.5")
	implementation("org.mariadb.jdbc:mariadb-java-client:3.3.2")
	implementation("software.amazon.awssdk:s3:2.24.12")
	implementation(project(":shared"))
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
		archiveFileName = "gif-api.jar"
	}
	build {
		dependsOn(fatJar) // Trigger fat jar creation during build
	}
}