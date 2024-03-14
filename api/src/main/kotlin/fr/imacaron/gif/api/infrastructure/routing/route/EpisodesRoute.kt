package fr.imacaron.gif.api.infrastructure.routing.route

import fr.imacaron.gif.api.int
import fr.imacaron.gif.api.respond
import fr.imacaron.gif.api.infrastructure.routing.resources.API
import fr.imacaron.gif.api.models.search.Episode
import fr.imacaron.gif.api.models.search.Response
import fr.imacaron.gif.api.usecases.search.SearchEpisode
import fr.imacaron.gif.shared.NotFoundException
import fr.imacaron.gif.shared.infrastrucutre.repository.DbEpisodeRepository
import fr.imacaron.gif.shared.infrastrucutre.repository.DbSeasonRepository
import fr.imacaron.gif.shared.infrastrucutre.repository.DbSeriesRepository
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

class EpisodesRoute(
		application: Application,
		private val seriesRepository: DbSeriesRepository,
		private val seasonsRepository: DbSeasonRepository,
		private val episodeRepository: DbEpisodeRepository
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
			seriesRepository.getSeries(it.parent.parent.parent.name).onSuccess { series ->
				seasonsRepository.getSeriesSeason(series, it.parent.number).onSuccess { season ->
					episodeRepository.getSeasonEpisodes(season, page, pageSize).onSuccess { episodes ->
						call.respond(Response.Ok(episodes.map { e -> Episode(e) }))
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
		val searchEpisode = SearchEpisode(seriesRepository, seasonsRepository, episodeRepository)
		get<API.Series.Name.Seasons.Number.Episodes.Index> {
			searchEpisode(
					it.parent.parent.parent.parent.name,
					it.parent.parent.number,
					it.index
			).onSuccess { ep ->
				call.respond(Response.Ok(ep))
			}.onFailure { e ->
				when(e) {
					is NotFoundException -> call.respond(Response.NotFound)
					else -> call.respond(Response.ServerError)
				}
			}
		}
	}
}