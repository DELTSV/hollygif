package fr.imacaron.gif.api.models.discord

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordOAuth2(
		val application: Application,
		val scopes: List<String>,
		val expires: Instant,
		val user: DiscordUser
)

@Serializable
data class Application(
	val id: String,
	val name: String,
	val icon: String?,
	val description: String?,
	val hook: Boolean?,
	@SerialName("bot_public")
	val botPublic: Boolean?,
	@SerialName("bot_require_code_grant")
	val botRequireCodeGrant: Boolean?,
	@SerialName("verify_key")
	val verifyKey: String?
)