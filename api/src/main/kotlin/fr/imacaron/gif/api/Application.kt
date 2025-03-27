package fr.imacaron.gif.api

import dev.kord.core.Kord
import fr.imacaron.gif.api.plugins.*
import fr.imacaron.gif.api.routing.configureRouting
import fr.imacaron.gif.api.routing.route.*
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

	val transcriptionRepository = TranscriptionRepository(db)
	val gifRepository = GifRepository(db)
	val sceneRepository = SceneRepository(db)
	val episodeRepository = EpisodeRepository(db)
	val seasonRepository = SeasonRepository(db)
	val seriesRepository = SeriesRepository(db)
	FileRoute(this@module)
	TranscriptionRoute(seriesRepository, seasonRepository, episodeRepository, transcriptionRepository, this@module)
	GifRoute(seriesRepository, gifRepository, seasonRepository, episodeRepository, sceneRepository, transcriptionRepository, kord, this@module)
	ScenesRoute(this@module, seriesRepository, seasonRepository, episodeRepository, sceneRepository, transcriptionRepository)
	EpisodesRoute(this@module, seriesRepository, seasonRepository, episodeRepository)
	SeasonsRoute(this@module, seriesRepository, seasonRepository)
	SeriesRoute(this@module, seriesRepository)
	SearchRoute(this@module, seriesRepository, episodeRepository, gifRepository, transcriptionRepository, kord)
}
