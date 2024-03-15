package fr.imacaron.gif.api.infrastructure.plugins

import fr.imacaron.gif.api.models.discord.DiscordOAuth2
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import kotlinx.serialization.json.Json

fun Application.configureAuth() {
	val client = HttpClient(CIO) {
		install(Auth)
		install(ContentNegotiation) {
			json(Json {
				ignoreUnknownKeys = true
			})
		}
	}
	install(Authentication) {
		bearer("discord-token") {
			authenticate { tokenCred ->
				val response = client.get("https://discord.com/api/oauth2/@me") {
					headers {
						append("Authorization", "Bearer ${tokenCred.token}")
					}
				}
				if(response.status == HttpStatusCode.OK) {
					val data = response.body<DiscordOAuth2>()
					data.user.id?.let { UserIdPrincipal(it) }
				} else {
					null
				}
			}
		}
	}
}