package fr.imacaron.gif.api.infrastructure.routing

import fr.imacaron.gif.api.respond
import fr.imacaron.gif.api.models.search.Response
import fr.imacaron.gif.shared.InvalidParamException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
	install(Resources)
	install(StatusPages) {
		exception<InvalidParamException> { call, _ ->
			call.respond(Response.BadRequest)
		}
		exception<Throwable> { call, cause ->
			call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
		}
	}
	routing {
		get("/") {
			call.respondText("GIF API/APP")
		}
	}
}