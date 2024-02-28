package fr.imacaron.gif.api

import dev.kord.core.Kord
import fr.imacaron.gif.api.plugins.*
import fr.imacaron.gif.api.routing.configureRouting
import fr.imacaron.gif.api.routing.route.FileRoute
import fr.imacaron.gif.api.routing.route.GifRoute
import fr.imacaron.gif.api.routing.route.SeasonsRoute
import fr.imacaron.gif.api.routing.route.SeriesRoute
import fr.imacaron.gif.shared.repository.*
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
	io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() = runBlocking {
	configureHTTP()
	configureMonitoring()
	configureSerialization()
	configureRouting()
	configureAuth()

	val kord = Kord(System.getenv("TOKEN"))

	val db = configureDatabase()

	val gifRepository = GifRepository(db)
	val sceneRepository = SceneRepository(db)
	val episodeRepository = EpisodeRepository(db, sceneRepository)
	val seasonRepository = SeasonRepository(db, episodeRepository)
	val seriesRepository = SeriesRepository(db, seasonRepository, episodeRepository)
	FileRoute(gifRepository,this@module)
	GifRoute(seriesRepository, gifRepository, kord, this@module)
	SeasonsRoute(this@module, seriesRepository, seasonRepository)
	SeriesRoute(this@module, seriesRepository)
}
