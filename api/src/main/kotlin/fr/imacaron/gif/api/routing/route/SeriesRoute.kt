package fr.imacaron.gif.api.routing.route

import fr.imacaron.gif.api.respond
import fr.imacaron.gif.api.routing.resources.API
import fr.imacaron.gif.api.types.Response
import fr.imacaron.gif.api.types.Series
import fr.imacaron.gif.shared.NotFoundException
import fr.imacaron.gif.shared.logger
import fr.imacaron.gif.shared.repository.SeriesRepository
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*

class SeriesRoute(
	application: Application,
	private val seriesRepository: SeriesRepository
) {
	init {
		application.route()
	}

	private fun Application.route() {
		routing {
			getSeries()
			getOneSeries()
		}
	}

	private fun Route.getSeries() {
		get<API.Series> {
			seriesRepository.getSeries().onSuccess {series ->
				call.respond(Response.Ok(series.map { Series(it) }))
			}.onFailure {
				logger.error(it)
				call.respond(Response.ServerError)
			}
		}
	}

	private fun Route.getOneSeries() {
		get<API.Series.Name> {
			seriesRepository.getSeries(it.name).onSuccess { series ->
				call.respond(Response.Ok(Series(series)))
			}.onFailure { e ->
				when(e) {
					is NotFoundException -> call.respond(Response.NotFound)
					else -> call.respond(Response.ServerError)
				}
			}
		}
	}
}