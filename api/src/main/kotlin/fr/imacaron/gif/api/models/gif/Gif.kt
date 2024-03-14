package fr.imacaron.gif.api.models.gif

import dev.kord.core.entity.User
import fr.imacaron.gif.api.models.discord.DiscordUser
import fr.imacaron.gif.shared.infrastrucutre.repository.DbGifEntity
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Gif(
		val id: Int,
		val creator: DiscordUser,
		val file: String,
		val createdAt: Instant,
		val scene: Scene,
		val timecode: String,
		val text: String
) {
	constructor(entity: DbGifEntity, user: User?): this(
		entity.id,
		DiscordUser(user),
		"${entity.scene.episode.season.number}_${entity.scene.episode.number}_${entity.scene.index}_${entity.text.hashCode()}.gif",
		entity.date,
		Scene(entity.scene),
		entity.timecode,
		entity.text
	)
}
