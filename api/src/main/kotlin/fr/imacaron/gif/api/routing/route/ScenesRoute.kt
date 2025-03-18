package fr.imacaron.gif.api.routing.route

import fr.imacaron.gif.api.respond
import fr.imacaron.gif.api.routing.resources.API
import fr.imacaron.gif.api.types.Response
import fr.imacaron.gif.api.types.Scene
import fr.imacaron.gif.shared.NotFoundException
import fr.imacaron.gif.shared.entity.Series
import fr.imacaron.gif.shared.logger
import fr.imacaron.gif.shared.repository.EpisodeRepository
import fr.imacaron.gif.shared.repository.SceneRepository
import fr.imacaron.gif.shared.repository.SeriesRepository
import fr.imacaron.gif.shared.repository.SeasonRepository
import fr.imacaron.gif.shared.repository.TranscriptionRepository
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.*
import io.ktor.server.resources.*
import io.ktor.server.response.respondBytes
import io.ktor.util.logging.error

class ScenesRoute(
	application: Application,
	private val seriesRepository: SeriesRepository,
	private val seasonRepository: SeasonRepository,
	private val episodeRepository: EpisodeRepository,
	private val sceneRepository: SceneRepository,
	private val transcriptionRepository: TranscriptionRepository
) {
	init {
		application.route()
	}

	private fun Application.route() {
		routing {
			getEpisodeScenes()
			getSceneFile()
		}
	}

	private fun Route.getEpisodeScenes() {
		get<API.Series.Name.Seasons.Number.Episodes.Index.Scenes> {
			seriesRepository.getSeries(it.episodeIndex.episodes.seasonNumber.seasons.seriesName.name).onSuccess { series ->
				seasonRepository.getSeriesSeason(series, it.episodeIndex.episodes.seasonNumber.number).onSuccess { season ->
					episodeRepository.getSeasonEpisode(season, it.episodeIndex.index).onSuccess { episode ->
						sceneRepository.getEpisodeScenes(episode).onSuccess { scenes ->
							call.respond(Response.Ok(scenes.map { Scene(it) }))
						}.onFailure {
							call.respond(Response.ServerError)
						}
					}.onFailure { e ->
						when(e) {
							is NotFoundException -> call.respond(Response.NotFound)
							else -> call.respond(Response.ServerError)
						}
					}
				}.onFailure {e ->
					when(e) {
						is NotFoundException -> call.respond(Response.NotFound)
						else -> call.respond(Response.ServerError)
					}
				}
			}
		}
	}

	private fun Route.getSceneFile() {
		get<API.Series.Name.Seasons.Number.Episodes.Index.Scenes.SceneIndex.File> {
			val sceneIndex = it.sceneIndex.sceneIndex
			val episodeNumber = it.sceneIndex.scenes.episodeIndex.index
			val seasonNumber = it.sceneIndex.scenes.episodeIndex.episodes.seasonNumber.number
			val seriesName = it.sceneIndex.scenes.episodeIndex.episodes.seasonNumber.seasons.seriesName.name
			seriesRepository.getSeries(seriesName).onSuccess {
				val series = Series(seasonRepository, episodeRepository, sceneRepository, transcriptionRepository, it)
				try {
					series.seasons[seasonNumber].episodes[episodeNumber].scenes[sceneIndex].makeScene().onSuccess { scene ->
						call.respondBytes(scene, ContentType("video", "webm"), HttpStatusCode.OK)
					}.onFailure {
						logger.error(it)
						call.respond(Response.ServerError)
					}
				} catch (_: Exception) {
					call.respond(Response.NotFound)
					return@onSuccess
				}
			}.onFailure {e ->
				when(e) {
					is NotFoundException -> call.respond(Response.NotFound)
					else -> call.respond(Response.ServerError)
				}
			}
		}
	}
}