package fr.imacaron.mobile.gif.viewmodel

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import fr.imacaron.mobile.gif.Json
import fr.imacaron.mobile.gif.types.Response
import fr.imacaron.mobile.gif.types.SearchResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

enum class SearchType(val type: String?, val label: String, val index: Int) {
	All(null, "Tout", 0),
	Episode("episode", "Épisodes", 1),
	Gif("gif", "Gifs", 2),
	Texte("transcription", "Textes", 3)
}

class SearchViewModel: ViewModel() {
	private val client = HttpClient {
		install(ContentNegotiation) {
			json(Json)
		}
	}

	var result: MutableList<SearchResult> = mutableStateListOf()
	private set

	private var page = 0

	var searchText by mutableStateOf("")

	var selectedType: SearchType by mutableStateOf(SearchType.All)

	val gridState = LazyGridState()

	suspend fun search() {
		page = 0
		withContext(Dispatchers.IO) {
			val req = client.get("https://gif.imacaron.fr/api/search/$searchText${selectedType.type?.let { "?type=${selectedType.type}&page=$page" } ?: ""}")
			if (req.status.isSuccess()) {
				result.clear()
				result.addAll(req.body<Response<List<SearchResult>>>().data)
			} else {
				// error
			}
		}
	}

	suspend fun loadMore() {
		if(result[0].data.size >= result[0].total || selectedType.type == null) {
			return
		}
		page++
		withContext(Dispatchers.IO) {
			val req = client.get("https://gif.imacaron.fr/api/search/$searchText${selectedType.type?.let { "?type=${selectedType.type}&page=$page" } ?: ""}")
			if (req.status.isSuccess()) {
				if(result.size != 1) {
					return@withContext
				}
				val newData = req.body<Response<List<SearchResult>>>().data
				result[0] = result[0].copy(data = result[0].data + newData[0].data)
			} else {
				// error
			}
		}
	}
}