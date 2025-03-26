package fr.imacaron.mobile.gif.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import fr.imacaron.mobile.gif.Json
import fr.imacaron.mobile.gif.types.DiscordUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json

class DiscordViewModel(pref: DataStore<Preferences>): AuthViewModel(pref) {
	private val client = HttpClient {
		install(ContentNegotiation) {
			json(Json)
		}
	}

	var user by mutableStateOf<DiscordUser?>(null)

	suspend fun fetchUser() {
		val token = getToken()
		client.get("https://discord.com/api/v10/users/@me") {
			header("Authorization", "Bearer $token")
		}.apply {
			if(status == HttpStatusCode.OK) {
				user = body<DiscordUser>()
			}
		}
	}
}