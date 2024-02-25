package fr.imacaron.gif.api

import fr.imacaron.gif.api.plugins.configureDatabase
import fr.imacaron.gif.api.plugins.configureHTTP
import fr.imacaron.gif.api.plugins.configureMonitoring
import fr.imacaron.gif.api.plugins.configureSerialization
import fr.imacaron.gif.api.routing.configureRouting
import fr.imacaron.gif.api.routing.route.FileRoute
import fr.imacaron.gif.api.routing.route.GifRoute
import fr.imacaron.gif.shared.repository.*
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
	val sceneRepository = SceneRepository(db)
	val episodeRepository = EpisodeRepository(db, sceneRepository)
	val seasonRepository = SeasonRepository(db, episodeRepository)
	val seriesRepository = SeriesRepository(db, seasonRepository, episodeRepository)
	FileRoute(gifRepository,this)
	GifRoute(seriesRepository, gifRepository, this)
}
