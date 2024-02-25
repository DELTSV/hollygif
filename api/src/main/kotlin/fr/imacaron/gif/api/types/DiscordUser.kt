package fr.imacaron.gif.api.types

import dev.kord.core.entity.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordUser(
	val id: String?,
	val username: String?,
	val discriminator: String?,
	@SerialName("global_name")
	val globalName: String?,
	val avatar: String?
) {
	constructor(user: User?): this(
		user?.id?.toString(),
		user?.username,
		user?.discriminator,
		user?.globalName,
		user?.avatar?.cdnUrl?.toUrl()
	)
}
