package fr.imacaron.gif.api.types

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
	val code: Int,
	val message: String,
	val data: T
) {
	companion object {
		fun <T>Ok(data: T) = Response(200, "OK", data)
		val NotFound = Response<String?>(404, "Not Found", null)
	}
}