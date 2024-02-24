package fr.imacaron.gif.api.types

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
	val code: Int,
	val message: String,
	val data: T
) {
	companion object {
		val NotFound = Response<String?>(404, "Not Found", null)
	}
}