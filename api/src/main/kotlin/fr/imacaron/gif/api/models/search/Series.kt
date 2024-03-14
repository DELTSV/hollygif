package fr.imacaron.gif.api.models.search

import fr.imacaron.gif.shared.infrastrucutre.repository.SeriesEntity
import kotlinx.serialization.Serializable

@Serializable
data class Series(
	val id: Int,
	val name: String,
	val logo: String
) {
	constructor(entity: SeriesEntity): this(
		entity.id,
		entity.name,
		entity.logo
	)
}
