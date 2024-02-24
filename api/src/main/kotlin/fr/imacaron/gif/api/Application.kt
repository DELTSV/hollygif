package fr.imacaron.gif.api

import fr.imacaron.gif.api.plugins.configureDatabase
import fr.imacaron.gif.api.plugins.configureHTTP
import fr.imacaron.gif.api.plugins.configureMonitoring
import fr.imacaron.gif.api.plugins.configureSerialization
import fr.imacaron.gif.api.routing.configureRouting
import fr.imacaron.gif.api.routing.route.FileRoute
import fr.imacaron.gif.shared.repository.GifRepository
import io.ktor.server.application.*

fun main(args: Array<String>) {
	io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
	configureHTTP()
	configureMonitoring()
	configureSerialization()
	configureRouting()

	val db = configureDatabase()

	val gifRepository = GifRepository(db)
	FileRoute(gifRepository,this)
}
