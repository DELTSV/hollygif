package fr.imacaron.gif.api.models.gif

import fr.imacaron.gif.api.models.search.Episode
import fr.imacaron.gif.shared.infrastrucutre.repository.SceneEntity
import kotlinx.serialization.Serializable

@Serializable
data class Scene(
	val id: Int,
	val index: Int,
	val start: Double,
	val end: Double,
	val episode: Episode
) {
	constructor(entity: SceneEntity): this(
		entity.id,
		entity.index,
		entity.start,
		entity.end,
		Episode(entity.episode)
	)
}
