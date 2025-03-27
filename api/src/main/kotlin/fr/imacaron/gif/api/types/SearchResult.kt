package fr.imacaron.gif.api.types

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SearchResult(
	val total: Int,
	val showed: Int,
	val page: Int,
	val type: String,
	val data: List<Searchable>
)

@Serializable
sealed interface Searchable {
	companion object {
		val type: String = "searchable"
	}
}