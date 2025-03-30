package fr.imacaron.gif.api.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureHTTP() {
	install(CORS) {
		allowMethod(HttpMethod.Options)
		allowMethod(HttpMethod.Put)
		allowMethod(HttpMethod.Delete)
		allowMethod(HttpMethod.Patch)
		allowMethod(HttpMethod.Post)
		allowMethod(HttpMethod.Get)
		allowHeader(HttpHeaders.Authorization)
		allowHeader(HttpHeaders.AccessControlAllowOrigin)
//		allowHost("app.gif.imacaron.fr", listOf("https"))
		anyHost()
	}
	install(ForwardedHeaders) // WARNING: for security, do not include this if not behind a reverse proxy
	install(XForwardedHeaders) // WARNING: for security, do not include this if not behind a reverse proxy
	routing {
		swaggerUI(path = "openapi")
	}
}
