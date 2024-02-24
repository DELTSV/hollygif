package fr.imacaron.gif.api.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
	install(Resources)
	install(StatusPages) {
		exception<Throwable> { call, cause ->
			call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
		}
	}
	routing {
		get<Gif.File> {
			call.respond("Euh ça marche avec /api ?")
		}
		get("/") {
			call.respondText("GIF API/APP")
		}
	}
}