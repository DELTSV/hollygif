package fr.imacaron.gif.api.infrastructure.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization(devMode: Boolean = false) {
	install(ContentNegotiation) {
		json(Json {
			encodeDefaults = false
			prettyPrint = devMode
			ignoreUnknownKeys = true
		})
	}
}
