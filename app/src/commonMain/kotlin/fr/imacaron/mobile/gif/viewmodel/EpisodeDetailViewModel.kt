package fr.imacaron.mobile.gif.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import fr.imacaron.mobile.gif.Json
import fr.imacaron.mobile.gif.types.CreateGif
import fr.imacaron.mobile.gif.types.Episode
import fr.imacaron.mobile.gif.types.Gif
import fr.imacaron.mobile.gif.types.Response
import fr.imacaron.mobile.gif.types.Scene
import fr.imacaron.mobile.gif.types.SceneStatus
import fr.imacaron.mobile.gif.types.Transcription
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.plugins.sse.sse
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.collections.plus

class EpisodeDetailViewModel(
	private val episode: Episode,
	pref: DataStore<Preferences>
): AuthViewModel(pref) {
	var gifs: Set<Gif> by mutableStateOf(setOf<Gif>())
		private set

	var transcriptions by mutableStateOf(listOf<Transcription>())
	private set

	var scenes by mutableStateOf(listOf<Scene>())
	private set

	var currentScene by mutableStateOf(0)

	val client = HttpClient {
		install(ContentNegotiation) {
			json(Json)
		}
		install(SSE)
	}

	var status by mutableStateOf("")
	var gifId by mutableStateOf<Int?>(null)

	private var gifPage = -1

	suspend fun fetchGif(page: Int) {
		withContext(Dispatchers.IO) {
			if (gifPage < page) {
				gifPage = page
			}
			val response =
				client.get("https://gif.imacaron.fr/api/series/${episode.season.series.name}/seasons/${episode.season.number}/episodes/${episode.number}/gif?page=$page&page_size=10")
					.body<Response<List<Gif>>>()
			if (response.code == 200) {
				gifs += response.data
			} else {
				//alert
			}
		}
	}

	suspend fun nextGifPage() {
		if(gifPage == -1) {
			throw IllegalStateException("Call fetchGif(page) first")
		}
		fetchGif(gifPage + 1)
	}

	suspend fun fetchTranscriptions() {
		withContext(Dispatchers.IO) {
			val response = client.get("https://gif.imacaron.fr/api/series/${episode.season.series.name}/seasons/${episode.season.number}/episodes/${episode.number}/transcriptions")
				.body<Response<List<Transcription>>>()
			if(response.code == 200) {
				transcriptions = response.data
			}
		}
	}

	suspend fun fetchScenes() {
		withContext(Dispatchers.IO) {
			val response = client.get("https://gif.imacaron.fr/api/series/${episode.season.series.name}/seasons/${episode.season.number}/episodes/${episode.number}/scenes")
			if(response.status == HttpStatusCode.OK) {
				scenes = response.body<Response<List<Scene>>>().data
			} else {
				//error
			}
		}
	}

	suspend fun createGif(text: String, textSize: Int) {
		val token = getToken() ?: return
		withContext(Dispatchers.IO) {
			client.sse(request = {
				method = HttpMethod.Post
				url {
					protocol = URLProtocol.HTTPS
					host = "gif.imacaron.fr"
					path("/api/gif")
				}
				contentType(ContentType.Application.Json)
				setBody(Json.encodeToString(CreateGif(scenes[currentScene], text, textSize)))
				header(HttpHeaders.Authorization, "Bearer $token")
			}) {
				while (true) {
					incoming.collect {
						it.data?.let { str ->
							Json.decodeFromString<SceneStatus>(str).let { sceneStatus ->
								if(sceneStatus.gifId != null) {
									gifId = sceneStatus.gifId
								} else if(sceneStatus.gif) {
									status = "Le gif est prêt"
								} else if(sceneStatus.text) {
									status = "Le texte est prêt"
								} else if(sceneStatus.textLength) {
									status = "Les mesures sont prises"
								} else if(sceneStatus.scene) {
									status = "La scène est prête"
								}
							}
						}
					}
				}
			}
		}
	}

	suspend fun getGif(id: Int): Gif? {
		val response = client.get("https://gif.imacaron.fr/api/gif/$id")
		return if(response.status == HttpStatusCode.OK) {
			response.body<Response<Gif>>().data
		} else {
			null
		}
	}

	suspend fun getTextHeight(text: String, textSize: Int): Int? {
		val token = getToken() ?: return null
		val response = client.post("https://gif.imacaron.fr/api/gif/text") {
			contentType(ContentType.Application.Json)
			setBody(CreateGif(scenes[currentScene], text, textSize))
			header(HttpHeaders.Authorization, "Bearer $token")
		}
		return if(response.status == HttpStatusCode.OK) {
			response.body<Response<Double>>().data.toInt()
		} else {
			null
		}
	}
}