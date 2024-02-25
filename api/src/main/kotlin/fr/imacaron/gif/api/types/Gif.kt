package fr.imacaron.gif.api.types

import fr.imacaron.gif.shared.repository.GifEntity
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Gif(
	val id: Int,
	val creator: String,
	val file: String,
	val createdAt: Instant,
	val scene: Scene,
	val timecode: String,
	val text: String
) {
	constructor(entity: GifEntity): this(
		entity.id,
		entity.user,
		"${entity.scene.episode.season.number}_${entity.scene.episode.number}_${entity.scene.index}_${entity.text.hashCode()}.gif",
		entity.date,
		Scene(entity.scene),
		entity.timecode,
		entity.text
	)
}
