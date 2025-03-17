package fr.imacaron.gif.api.routing.route

import fr.imacaron.gif.api.int
import fr.imacaron.gif.api.respond
import fr.imacaron.gif.api.routing.resources.API
import fr.imacaron.gif.api.types.Episode
import fr.imacaron.gif.api.types.Response
import fr.imacaron.gif.shared.NotFoundException
import fr.imacaron.gif.shared.repository.EpisodeRepository
import fr.imacaron.gif.shared.repository.SeasonRepository
import fr.imacaron.gif.shared.repository.SeriesRepository
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

class EpisodesRoute(
	application: Application,
	private val seriesRepository: SeriesRepository,
	private val seasonsRepository: SeasonRepository,
	private val episodeRepository: EpisodeRepository
) {
	init {
		application.route()
	}

	private fun Application.route() {
		routing {
			getSeasonEpisodes()
			getOneSeasonEpisode()
		}
	}

	private fun Route.getSeasonEpisodes() {
		get<API.Series.Name.Seasons.Number.Episodes> {
			val page = call.request.queryParameters.int("page") ?: 0
			val pageSize = call.request.queryParameters.int("page_size") ?: 12
			seriesRepository.getSeries(it.seasonNumber.seasons.seriesName.name).onSuccess { series ->
				seasonsRepository.getSeriesSeason(series, it.seasonNumber.number).onSuccess { season ->
					episodeRepository.getSeasonEpisodes(season, page, pageSize).onSuccess { episodes ->
						call.respond(Response.Ok(episodes.map { (e, total) -> Episode(e, total) }))
					}.onFailure { e ->
						when(e) {
							is NotFoundException -> call.respond(Response.NotFound)
							else -> call.respond(Response.ServerError)
						}
					}
				}.onFailure { e ->
					when(e) {
						is NotFoundException -> call.respond(Response.NotFound)
						else -> call.respond(Response.ServerError)
					}
				}
			}.onFailure { e ->
				when(e) {
					is NotFoundException -> call.respond(Response.NotFound)
					else -> call.respond(Response.ServerError)
				}
			}
		}
	}

	private fun Route.getOneSeasonEpisode() {
		get<API.Series.Name.Seasons.Number.Episodes.Index> {
			seriesRepository.getSeries(it.episodes.seasonNumber.seasons.seriesName.name).onSuccess { series ->
				seasonsRepository.getSeriesSeason(series, it.episodes.seasonNumber.number).onSuccess { season ->
					episodeRepository.getSeasonEpisode(season, it.index).onSuccess { episode ->
						episodeRepository.getEpisodeGifTotal(episode).onSuccess { total ->
							call.respond(Response.Ok(Episode(episode, total)))
						}.onFailure {
							call.respond(Response.ServerError)
						}
					}.onFailure { e ->
						when(e) {
							is NotFoundException -> call.respond(Response.NotFound)
							else -> call.respond(Response.ServerError)
						}
					}
				}.onFailure { e ->
					when(e) {
						is NotFoundException -> call.respond(Response.NotFound)
						else -> call.respond(Response.ServerError)
					}
				}
			}.onFailure { e ->
				when(e) {
					is NotFoundException -> call.respond(Response.NotFound)
					else -> call.respond(Response.ServerError)
				}
			}
		}
	}
}