package fr.imacaron.mobile.gif.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import fr.imacaron.mobile.gif.Json
import fr.imacaron.mobile.gif.types.Response
import fr.imacaron.mobile.gif.types.Series
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json

class SeriesViewModel: ViewModel() {
	var series by mutableStateOf(setOf<Series>())
	private set

	private val client = HttpClient {
		install(ContentNegotiation) {
			json(Json)
		}
	}

	suspend fun fetch(page: Int) {
		val response = client.get("https://gif.imacaron.fr/api/series?page=$page").body<Response<List<Series>>>()
		if(response.code == 200) {
			series += response.data.toSet()
		}
	}
}