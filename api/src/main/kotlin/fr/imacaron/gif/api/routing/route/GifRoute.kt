package fr.imacaron.gif.api.routing.route

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.User
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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

class GifRoute(
	seriesRepository: SeriesRepository,
	private val gifRepository: GifRepository,
	private val kord: Kord,
	application: Application
) {
	private lateinit var kaamelott: Series

	@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
	private val usersContext = newSingleThreadContext("UsersContext")
	private val users: MutableMap<String, User> = mutableMapOf()

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
				val user = users[it.entity.user] ?: withContext(usersContext) {
					kord.getUser(Snowflake(it.entity.user))?.let {u ->
						users[it.entity.user] = u
						u
					}
				}
				Gif(it.entity, user)
			}
			call.respond(Response.Ok(gifs))
		}
	}
}