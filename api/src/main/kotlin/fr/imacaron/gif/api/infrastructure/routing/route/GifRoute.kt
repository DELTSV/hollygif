package fr.imacaron.gif.api.infrastructure.routing.route

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.User
import fr.imacaron.gif.api.int
import fr.imacaron.gif.api.respond
import fr.imacaron.gif.api.infrastructure.routing.resources.API
import fr.imacaron.gif.api.models.gif.Gif
import fr.imacaron.gif.api.models.search.Response
import fr.imacaron.gif.api.usecases.gif.CreateGif
import fr.imacaron.gif.api.usecases.gif.GifCreation
import fr.imacaron.gif.shared.NotFoundException
import fr.imacaron.gif.shared.infrastrucutre.FileManager
import fr.imacaron.gif.shared.search.Series
import fr.imacaron.gif.shared.infrastrucutre.repository.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

class GifRoute(
		private val seriesRepository: DbSeriesRepository,
		private val gifRepository: DbGifRepository,
		private val seasonRepository: DbSeasonRepository,
		private val episodeRepository: DbEpisodeRepository,
		private val sceneRepository: DbSceneRepository,
		private val kord: Kord,
		private val fileManager: FileManager,
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
			kaamelott = Series(seasonRepository, episodeRepository, sceneRepository, it)
		}
	}

	private fun Application.route() {
		routing {
			getGifList()
			getGif()
			authenticate("discord-token") {
				getMyGif()
				createGif()
			}
		}
	}

	private fun Route.getGifList() {
		get<API.Gif> {
			val page = call.request.queryParameters.int("page") ?: 0
			val pageSize = call.request.queryParameters.int("page_size") ?: 12
			val gifs = gifRepository.getSeriesGifs(kaamelott.entity, page, pageSize).map {
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

	private fun Route.getGif() {
		get<API.Gif.ID> { gifId ->
			val id = gifId.id
			gifRepository.getGif(id).onSuccess {
				val user = users[it.entity.user] ?: withContext(usersContext) {
					kord.getUser(Snowflake(it.entity.user))?.let { u ->
						users[it.entity.user] = u
						u
					}
				}
				call.respond(Response.Ok(Gif(it.entity, user)))
			}
		}
	}

	private fun Route.getMyGif() {
		get<API.Gif.Me> {
			val id = call.principal<UserIdPrincipal>()?.name ?: run {
				call.respond(Response.Unauthorized)
				return@get
			}
			val page = call.request.queryParameters.int("page") ?: 0
			val pageSize = call.request.queryParameters.int("page_size") ?: 12
			val gifs = gifRepository.getUserGifs(id, page, pageSize).map {
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

	private fun Route.createGif() {
		val createGif = CreateGif(fileManager, seriesRepository, seasonRepository, episodeRepository, sceneRepository)
		post<API.Gif> {
			val gifCreate: GifCreation = call.receive()
			createGif(gifCreate, 156)
		}
	}
}