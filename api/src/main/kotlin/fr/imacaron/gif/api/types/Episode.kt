package fr.imacaron.gif.api.types

import fr.imacaron.gif.shared.repository.EpisodeEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("episode")
data class Episode(
	val id: Int,
	val number: Int,
	val width: Int,
	val height: Int,
	val fps: Int,
	val title: String,
	val season: Season,
	val duration: Double,
	val numberOfGif: Int? = null
): Searchable {
	constructor(entity: EpisodeEntity, numberOfGif: Int? = null): this(
		entity.id,
		entity.number,
		entity.width,
		entity.height,
		entity.fps,
		entity.title,
		Season(entity.season),
		entity.duration,
		numberOfGif
	)

	companion object {
		val type = "episode"
	}
}
