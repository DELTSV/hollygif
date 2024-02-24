package fr.imacaron.gif

import fr.imacaron.gif.plugins.*
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
