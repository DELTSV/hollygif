package fr.imacaron.gif.api.routing.route

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.User
import fr.imacaron.gif.api.respond
import fr.imacaron.gif.api.routing.resources.API
import fr.imacaron.gif.api.types.Episode
import fr.imacaron.gif.api.types.Gif
import fr.imacaron.gif.api.types.Response
import fr.imacaron.gif.api.types.SearchResult
import fr.imacaron.gif.api.types.Series
import fr.imacaron.gif.api.types.Transcription
import fr.imacaron.gif.shared.repository.EpisodeRepository
import fr.imacaron.gif.shared.repository.GifRepository
import fr.imacaron.gif.shared.repository.SeriesRepository
import fr.imacaron.gif.shared.repository.TranscriptionRepository
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import kotlin.collections.set

class SearchRoute(
	application: Application,
	private val seriesRepository: SeriesRepository,
	private val episodesRepository: EpisodeRepository,
	private val gifRepository: GifRepository,
	private val transcriptionRepository: TranscriptionRepository,
	private val kord: Kord,
) {
	@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
	private val usersContext = newSingleThreadContext("UsersContext")
	private val users: MutableMap<String, User> = mutableMapOf()

	init {
		application.route()
	}

	private fun Application.route() {
		routing {
			search()
		}
	}

	private fun Route.search() {
		get<API.Search> {
			val result = mutableListOf<SearchResult>()
			val search = "%${it.search.replace(" ", "%")}%"
			val type = call.request.queryParameters["type"]
			val page = if(type != null) call.request.queryParameters["page"]?.toIntOrNull() ?: 0 else 0
			val pageSize = if(type != null) call.request.queryParameters["page_size"]?.toIntOrNull() ?: 10 else 3
			when(type) {
				Episode.type -> {
					searchEpisodes(search, page, pageSize)?.let { result.add(it) }
				}

				Gif.type -> {
					searchGif(search, page, pageSize).let { result.add(it) }
				}

				Series.type -> {
					searchSeries(search, page, pageSize)?.let { result.add(it) }
				}

				Transcription.type -> {
					searchTranscriptions(search, page, pageSize)?.let { result.add(it) }
				}

				null -> {
					searchEpisodes(search, page, pageSize)?.let { result.add(it) }
					searchGif(search, page, pageSize).let { result.add(it) }
					searchSeries(search, page, pageSize)?.let { result.add(it) }
					searchTranscriptions(search, page, pageSize)?.let { result.add(it) }
				}
			}
			call.respond(Response.Ok(result))
		}
	}

	private fun searchEpisodes(search: String, page: Int, pageSize: Int): SearchResult? {
		return episodesRepository.searchEpisodeByTitle(search, page, pageSize).getOrNull()?.let { (episodes, total) ->
			SearchResult(
				total,
				pageSize,
				page,
				episodes.map { Episode(it) }
			)
		}
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	private suspend fun searchGif(search: String, page: Int, pageSize: Int): SearchResult {
		return gifRepository.searchGifsByText(search, page, pageSize).let { (gifs, total) ->
			SearchResult(
				total,
				pageSize,
				page,
				gifs.map {
					val user = users[it.user] ?: withContext(usersContext) {
						kord.getUser(Snowflake(it.user))?.also { u ->
							users[it.user] = u
						}
					}
					Gif(it, user)
				}
			)
		}
	}

	private fun searchSeries(search: String, page: Int, pageSize: Int): SearchResult? {
		return seriesRepository.searchSeriesByName(search, page, pageSize).getOrNull()?.let { (series, total) ->
			SearchResult(
				total,
				pageSize,
				page,
				series.map { Series(it) }
			)
		}
	}

	private fun searchTranscriptions(search: String, page: Int, pageSize: Int): SearchResult? {
		return transcriptionRepository.searchTextInTranscription(search, page, pageSize).getOrNull()?.let { (transcriptions, total) ->
			SearchResult(
				total,
				pageSize,
				page,
				transcriptions.map { Transcription(it) }
			)
		}
	}
}