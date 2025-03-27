package fr.imacaron.gif.api.types

import dev.kord.core.entity.User
import fr.imacaron.gif.shared.repository.GifEntity
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("gif")
data class Gif(
	val id: Int,
	val creator: DiscordUser,
	val file: String,
	val createdAt: Instant,
	val scene: Scene,
	val timecode: String,
	val text: String
): Searchable {
	constructor(entity: GifEntity, user: User?): this(
		entity.id,
		DiscordUser(user),
		"${entity.scene.episode.season.number}_${entity.scene.episode.number}_${entity.scene.index}_${entity.text.hashCode()}.gif",
		entity.date,
		Scene(entity.scene),
		entity.timecode,
		entity.text
	)

	companion object {
		val type = "gif"
	}
}
