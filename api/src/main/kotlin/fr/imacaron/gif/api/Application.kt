package fr.imacaron.gif.api

import dev.kord.core.Kord
import fr.imacaron.gif.api.plugins.*
import fr.imacaron.gif.api.infrastructure.routing.configureRouting
import fr.imacaron.gif.api.infrastructure.routing.route.*
import fr.imacaron.gif.shared.infrastrucutre.repository.DbGifRepository
import fr.imacaron.gif.shared.infrastrucutre.repository.DbSceneRepository
import fr.imacaron.gif.shared.infrastrucutre.repository.DbEpisodeRepository
import fr.imacaron.gif.shared.infrastrucutre.repository.DbSeasonRepository
import fr.imacaron.gif.shared.infrastrucutre.repository.DbSeriesRepository
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

	val gifRepository = DbGifRepository(db)
	val sceneRepository = DbSceneRepository(db)
	val episodeRepository = DbEpisodeRepository(db)
	val seasonRepository = DbSeasonRepository(db)
	val seriesRepository = DbSeriesRepository(db)
	FileRoute(this@module)
	GifRoute(seriesRepository, gifRepository, seasonRepository, episodeRepository, sceneRepository, kord, this@module)
	EpisodesRoute(this@module, seriesRepository, seasonRepository, episodeRepository)
	SeasonsRoute(this@module, seriesRepository, seasonRepository)
	SeriesRoute(this@module, seriesRepository)
}
