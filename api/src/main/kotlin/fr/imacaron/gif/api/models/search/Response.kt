package fr.imacaron.gif.api.models.search

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
	val code: Int,
	val message: String,
	val data: T
) {
	companion object {
		fun <T>Ok(data: T) = Response(200, "OK", data)
		val BadRequest = Response<String?>(400, "Bad Request", null)
		val Unauthorized = Response<String?>(code = 401, "Unauthorized", null)
		val NotFound = Response<String?>(404, "Not Found", null)
		val ServerError = Response<String?>(500, "InternalServerError", null)
	}
}