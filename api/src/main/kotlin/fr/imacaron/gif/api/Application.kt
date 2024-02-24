package fr.imacaron.gif.api

import fr.imacaron.gif.api.plugins.configureHTTP
import fr.imacaron.gif.api.plugins.configureMonitoring
import fr.imacaron.gif.api.plugins.configureRouting
import fr.imacaron.gif.api.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
	io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
	configureHTTP()
	configureMonitoring()
	configureSerialization()
	configureRouting()
}
