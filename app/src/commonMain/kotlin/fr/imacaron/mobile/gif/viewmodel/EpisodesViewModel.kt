package fr.imacaron.mobile.gif.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import fr.imacaron.mobile.gif.Json
import fr.imacaron.mobile.gif.types.Episode
import fr.imacaron.mobile.gif.types.Response
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json

class EpisodesViewModel(val seriesName: String, val seasonNumber: Int) : ViewModel() {
	var episodes by mutableStateOf(setOf<Episode>())
		private set

	val client = HttpClient {
		install(ContentNegotiation) {
			json(Json)
		}
	}

	private var lastPage = -1

	suspend fun fetch(page: Int) {
		if(lastPage < page) {
			lastPage = page
		}
		val response = client.get("https://gif.imacaron.fr/api/series/$seriesName/seasons/$seasonNumber/episodes?page=$page&page_size=10")
			.body<Response<List<Episode>>>()
		if (response.code == 200) {
			episodes += response.data
		} else {
			//alert
		}
	}

	suspend fun nextPage() {
		if(lastPage == -1) {
			throw IllegalStateException("Call fetch(page) first")
		}
		fetch(lastPage + 1)
	}

	private suspend fun fetchEpisode(number: Int): Episode? {
		val response = client.get("https://gif.imacaron.fr/api/series/$seriesName/seasons/$seasonNumber/episodes/$number")
			.body<Response<Episode>>()
		return if(response.code == 200) {
			response.data
		} else {
			null
		}
	}

	suspend fun getEpisode(number: Int): Episode? =
		episodes.find { it.number == number } ?: fetchEpisode(number)

}
