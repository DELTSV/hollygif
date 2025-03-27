package fr.imacaron.gif.api.types

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SearchResult<T: Searchable>(
	val total: Int,
	val showed: Int,
	val page: Int,
	val type: String,
	val data: List<T>
)

@Serializable
sealed interface Searchable {
	companion object {
		val type: String = "searchable"
	}
}