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
		val NoContent = Response<String?>(204, "No Content", null)
		val BadRequest = Response<String?>(400, "Bad Request", null)
		val Unauthorized = Response<String?>(code = 401, "Unauthorized", null)
		val Forbidden = Response<String?>(code = 403, "Forbidden", null)
		val NotFound = Response<String?>(404, "Not Found", null)
		val ServerError = Response<String?>(500, "InternalServerError", null)
	}
}