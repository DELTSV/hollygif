package fr.imacaron.mobile.gif.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import fr.imacaron.mobile.gif.Json
import fr.imacaron.mobile.gif.types.Episode
import fr.imacaron.mobile.gif.types.Gif
import fr.imacaron.mobile.gif.types.Response
import fr.imacaron.mobile.gif.types.Transcription
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.collections.plus

class EpisodeDetailViewModel(
	private val episode: Episode,
): ViewModel() {
	var gifs: Set<Gif> by mutableStateOf(setOf<Gif>())
		private set

	var transcriptions by mutableStateOf(listOf<Transcription>())

	val client = HttpClient {
		install(ContentNegotiation) {
			json(Json)
		}
	}

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
}