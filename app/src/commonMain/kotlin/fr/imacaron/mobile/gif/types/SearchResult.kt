package fr.imacaron.mobile.gif.types

import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
	val total: Int,
	val showed: Int,
	val page: Int,
	val type: String,
	val data: List<Searchable>
)

@Serializable
sealed interface Searchable