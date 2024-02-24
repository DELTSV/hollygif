package fr.imacaron.gif.api.routing

import fr.imacaron.gif.api.routing.route.FileRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {
	install(Resources)
	install(StatusPages) {
		exception<Throwable> { call, cause ->
			call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
		}
	}
	routing {
		route("/app") {
			singlePageApplication {
				react("front")
			}
		}
		get("/") {
			call.respondText("GIF API/APP")
		}
	}
}