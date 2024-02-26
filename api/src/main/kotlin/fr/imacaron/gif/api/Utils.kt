package fr.imacaron.gif.api

import fr.imacaron.gif.api.types.Response
import fr.imacaron.gif.shared.InvalidParamException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend inline fun <reified T : Any?> ApplicationCall.respond(message: Response<T>) {
	respond(HttpStatusCode(message.code, message.message), message)
}

fun Parameters.int(name: String): Int? = try {
	this[name]?.toInt()
} catch (_: NumberFormatException) {
	throw InvalidParamException()
}