package fr.imacaron.gif.api.types

import fr.imacaron.gif.shared.gif.DbSceneEntity
import kotlinx.serialization.Serializable

@Serializable
data class Scene(
	val id: Int,
	val index: Int,
	val start: Double,
	val end: Double,
	val episode: Episode
) {
	constructor(entity: DbSceneEntity): this(
		entity.id,
		entity.index,
		entity.start,
		entity.end,
		Episode(entity.episode)
	)
}
