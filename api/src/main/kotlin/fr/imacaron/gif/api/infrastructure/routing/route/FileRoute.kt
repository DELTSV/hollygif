package fr.imacaron.gif.api.infrastructure.routing.route

import fr.imacaron.gif.api.plugins.buildS3
import fr.imacaron.gif.api.respond
import fr.imacaron.gif.api.infrastructure.routing.resources.API
import fr.imacaron.gif.api.models.search.Response
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import software.amazon.awssdk.services.s3.model.NoSuchKeyException

class FileRoute(
	application: Application
) {
	init {
		application.route()
	}

	private fun Application.route() {
		val s3s = mutableMapOf("kaamelott" to buildS3("kaamelott"))
		routing {
			get<API.Gif.File> {
				try {
					val gif = s3s["kaamelott"]!!.getFile("gif", it.file)
					call.respondBytes(gif, ContentType.Any, HttpStatusCode.OK)
				}catch (_: NoSuchKeyException) {
					call.respond(Response.NotFound)
				}
			}
		}
	}
}