package fr.imacaron.mobile.gif.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import fr.imacaron.mobile.gif.types.Gif
import fr.imacaron.mobile.gif.types.Response
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json

class LastGifViewModel: ViewModel() {
	var lastGifs by mutableStateOf(setOf<Gif>())
	private set

	private val client = HttpClient {
		install(ContentNegotiation) {
			json()
		}
	}

	var lastPage = -1

	suspend fun fetch(page: Int): Boolean {
		if(lastPage < page) {
			lastPage = page
		}
		val response = client.get("https://gif.imacaron.fr/api/gif?page_size=12&page=$page").body<Response<List<Gif>>>()
		return if(response.code == 200) {
			lastGifs += response.data
			true
		} else {
			false
		}
	}

	suspend fun nextPage() {
		if(lastPage == -1) {
			throw IllegalStateException("Call fetch(page) first")
		}
		fetch(lastPage + 1)
	}

	fun delete(id: Int) {
		lastGifs = lastGifs.filter { it.id != id }.toSet()
	}
}