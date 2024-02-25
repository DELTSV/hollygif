package fr.imacaron.gif.api.types

import fr.imacaron.gif.shared.repository.SeriesEntity
import kotlinx.serialization.Serializable

@Serializable
data class Series(
	val id: Int,
	val name: String
) {
	constructor(entity: SeriesEntity): this(
		entity.id,
		entity.name
	)
}
