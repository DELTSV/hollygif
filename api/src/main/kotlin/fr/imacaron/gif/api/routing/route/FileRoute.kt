package fr.imacaron.gif.api.routing.route

import fr.imacaron.gif.api.routing.resources.API
import fr.imacaron.gif.api.types.Response
import fr.imacaron.gif.shared.repository.GifRepository
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

class FileRoute(
	val gifRepository: GifRepository,
	application: Application
) {
	init {
		application.route()
	}

	private fun Application.route() {
		routing {
			get<API.Gif.File> {
				val f = File("./gif/${it.file}")
				if(!f.exists()) {
					call.respond(Response.NotFound)
					return@get
				}
				call.respondFile(f)
			}
		}
	}
}