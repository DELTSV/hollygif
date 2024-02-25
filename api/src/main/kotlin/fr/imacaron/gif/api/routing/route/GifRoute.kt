package fr.imacaron.gif.api.routing.route

import fr.imacaron.gif.api.routing.resources.API
import fr.imacaron.gif.api.types.Gif
import fr.imacaron.gif.api.types.Response
import fr.imacaron.gif.shared.NotFoundException
import fr.imacaron.gif.shared.entity.Series
import fr.imacaron.gif.shared.repository.GifRepository
import fr.imacaron.gif.shared.repository.SeriesRepository
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class GifRoute(
	seriesRepository: SeriesRepository,
	private val gifRepository: GifRepository,
	application: Application
) {

	private lateinit var kaamelott: Series

	init {
		application.route()
		seriesRepository.getSeries("kaamelott").onFailure {
			throw NotFoundException("Kaamelott not found")
		}.onSuccess {
			kaamelott = it
		}
	}

	private fun Application.route() {
		routing {
			getGifList()
		}
	}

	private fun Route.getGifList() {
		get<API.Gif> {
			val gifs = gifRepository.getSeriesGifs(kaamelott.entity, 0).map {
				Gif(it.entity)
			}
			call.respond(Response.Ok(gifs))
		}
	}
}