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
	implementation(libs.kord.core)
	implementation(libs.kotlinx.datetime)
	implementation(libs.ktor.server.core.jvm)
	implementation(libs.ktor.server.resources)
	implementation(libs.ktor.server.host.common.jvm)
	implementation(libs.ktor.server.status.pages.jvm)
	implementation(libs.ktor.server.cors.jvm)
	implementation(libs.ktor.server.forwarded.header.jvm)
	implementation(libs.ktor.server.openapi)
	implementation(libs.ktor.server.swagger.jvm)
	implementation(libs.ktor.server.call.logging.jvm)
	implementation(libs.ktor.server.content.negotiation.jvm)
	implementation(libs.ktor.serialization.kotlinx.json.jvm)
	implementation(libs.ktor.server.cio.jvm)
	implementation(libs.ktor.server.auth)
	implementation(libs.ktor.ktor.client.core)
	implementation(libs.ktor.client.cio)
	implementation(libs.ktor.client.auth)
	implementation(libs.ktor.server.config.yaml)
	implementation("ch.qos.logback:logback-classic:$logback_version")
	implementation("ch.qos.logback:logback-core:$logback_version")
	implementation(libs.janino)
	implementation(libs.ktorm.core)
	implementation(libs.ktorm.support.mysql)
	implementation(libs.c3p0)
	implementation(libs.mariadb.java.client)
	implementation(libs.s3)
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