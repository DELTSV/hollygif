package fr.imacaron.gif.api.types

import fr.imacaron.gif.shared.repository.SeriesEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("series")
data class Series(
	val id: Int,
	val name: String,
	val logo: String
): Searchable {
	constructor(entity: SeriesEntity): this(
		entity.id,
		entity.name,
		entity.logo
	)

	companion object {
		val type = "series"
	}
}
