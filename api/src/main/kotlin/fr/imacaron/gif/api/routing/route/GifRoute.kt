package fr.imacaron.gif.api.routing.route

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.User
import fr.imacaron.gif.api.int
import fr.imacaron.gif.api.respond
import fr.imacaron.gif.api.routing.resources.API
import fr.imacaron.gif.api.types.CreateGif
import fr.imacaron.gif.api.types.Gif
import fr.imacaron.gif.api.types.Response
import fr.imacaron.gif.shared.NotFoundException
import fr.imacaron.gif.shared.entity.Series
import fr.imacaron.gif.shared.repository.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

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
			makeGifScenes()
			authenticate("discord-token") {
				getMyGif()
				makeGif()
			}
		}
	}

	private fun Route.makeGifScenes() {
		get("/api/gif/scenes") {
			val season = call.request.queryParameters["season"]?.toInt() ?: 1
			val episode = call.request.queryParameters["episode"]?.toInt() ?: 1
			with(kaamelott.seasons[season].episodes[episode]) {
				for(i in 0..<this.scenes.size) {
					scenes[i].makeScene()
				}
			}
			call.respond(Response.Ok("Generating"))
		}
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	private fun Route.makeGif() {
		post<API.Gif> {
			val body = runCatching { call.receive<CreateGif>() }.getOrElse {
				call.respond(Response.BadRequest)
				return@post
			}
			val userId = call.principal<UserIdPrincipal>()?.name ?: run {
				call.respond(Response.Unauthorized)
				return@post
			}
			val scene = kaamelott.seasons[body.scene.episode.season.number].episodes[body.scene.episode.number].scenes[body.scene.index]
			scene.createMeme(body.text).collect {
				it.result?.let {
					val gifEntity = GifEntity {
						this.scene = scene.entity
						this.date = Clock.System.now()
						this.text = text
						this.user = userId
						this.timecode = timecode
						this.status = GifStatus.SUCCESS
					}
					gifRepository.addGif(gifEntity)
					val user = users[userId] ?: withContext(usersContext) {
						kord.getUser(Snowflake(userId))?.let {u ->
							users[userId] = u
							u
						}
					}
					call.respond(Response.Ok(Gif(gifEntity, user)))
				}
			}
		}
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	private fun Route.getGifList() {
		get<API.Gif> {
			val page = call.request.queryParameters.int("page") ?: 0
			val pageSize = call.request.queryParameters.int("page_size") ?: 12
			val gifs = gifRepository.getSeriesGifs(kaamelott.entity, page, pageSize).map {
				val user = users[it.entity.user] ?: withContext(usersContext) {
					kord.getUser(Snowflake(it.entity.user))?.also { u ->
						users[it.entity.user] = u
					}
				}
				Gif(it.entity, user)
			}
			call.respond(Response.Ok(gifs))
		}
	}

	@OptIn(ExperimentalCoroutinesApi::class)
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

	@OptIn(ExperimentalCoroutinesApi::class)
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