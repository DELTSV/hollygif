package fr.imacaron.gif.api.routing.route

import fr.imacaron.gif.api.respond
import fr.imacaron.gif.api.routing.resources.API
import fr.imacaron.gif.api.types.Response
import fr.imacaron.gif.api.types.Season
import fr.imacaron.gif.shared.NotFoundException
import fr.imacaron.gif.shared.search.SeasonRepository
import fr.imacaron.gif.shared.search.SeriesRepository
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

class SeasonsRoute(
    application: Application,
    private val seriesRepository: SeriesRepository,
    private val seasonsRepository: SeasonRepository
) {
	init {
		application.route()
	}

	private fun Application.route() {
		routing {
			getSeriesSeasons()
			getOneSeriesSeason()
		}
	}

	private fun Route.getSeriesSeasons() {
		get<API.Series.Name.Seasons> {
			seriesRepository.getSeries(it.parent.name).onSuccess {  series ->
				seasonsRepository.getSeriesSeasons(series).onSuccess { seasons ->
					call.respond(Response.Ok(seasons.map { s -> Season(s) }))
				}.onFailure {
					call.respond(Response.ServerError)
				}
			}.onFailure { e ->
				when(e) {
					is NotFoundException -> call.respond(Response.NotFound)
					else -> call.respond(Response.ServerError)
				}
			}
		}
	}

	private fun Route.getOneSeriesSeason() {
		get<API.Series.Name.Seasons.Number> {
			seriesRepository.getSeries(it.parent.parent.name).onSuccess { series ->
				seasonsRepository.getSeriesSeason(series, it.number).onSuccess { season ->
					call.respond(Response.Ok(Season(season)))
				}.onFailure {
					call.respond(Response.ServerError)
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