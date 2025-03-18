package fr.imacaron.gif.api.routing.route

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.User
import fr.imacaron.gif.api.int
import fr.imacaron.gif.api.respond
import fr.imacaron.gif.api.routing.resources.API
import fr.imacaron.gif.api.types.Gif
import fr.imacaron.gif.api.types.Response
import fr.imacaron.gif.shared.NotFoundException
import fr.imacaron.gif.shared.entity.Series
import fr.imacaron.gif.shared.repository.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

class GifRoute(
	private val seriesRepository: SeriesRepository,
	private val gifRepository: GifRepository,
	private val seasonRepository: SeasonRepository,
	private val episodeRepository: EpisodeRepository,
	private val sceneRepository: SceneRepository,
	transcriptionRepository: TranscriptionRepository,
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
			kaamelott = Series(seasonRepository, episodeRepository, sceneRepository, transcriptionRepository, it)
		}
	}

	private fun Application.route() {
		routing {
			getGifList()
			getGif()
			getEpisodeGifs()
			authenticate("discord-token") {
				getMyGif()
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

	@OptIn(ExperimentalCoroutinesApi::class)
	private fun Route.getEpisodeGifs() {
		get<API.Series.Name.Seasons.Number.Episodes.Index.Gif> {
			val series = seriesRepository.getSeries(it.episodeIndex.episodes.seasonNumber.seasons.seriesName.name).getOrElse {
				call.respond(Response.NotFound)
				return@get
			}
			val season = seasonRepository.getSeriesSeason(series, it.episodeIndex.episodes.seasonNumber.number).getOrElse {
				call.respond(Response.NotFound)
				return@get
			}
			val episode = episodeRepository.getSeasonEpisode(season, it.episodeIndex.index).getOrElse {
				call.respond(Response.NotFound)
				return@get
			}
			val page = call.request.queryParameters.int("page") ?: 0
			val pageSize = call.request.queryParameters.int("page_size") ?: 12
			val gifs = gifRepository.getEpisodeGifs(episode, page, pageSize).map {
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