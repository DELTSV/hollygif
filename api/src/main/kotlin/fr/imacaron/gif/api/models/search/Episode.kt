package fr.imacaron.gif.api.models.search

import fr.imacaron.gif.shared.infrastrucutre.repository.EpisodeEntity
import kotlinx.serialization.Serializable

@Serializable
data class Episode(
		val id: Int,
		val number: Int,
		val width: Int,
		val height: Int,
		val fps: Int,
		val title: String,
		val season: Season,
		val duration: Double
) {
	constructor(entity: EpisodeEntity): this(
		entity.id,
		entity.number,
		entity.width,
		entity.height,
		entity.fps,
		entity.title,
		Season(entity.season),
		entity.duration
	)
}
