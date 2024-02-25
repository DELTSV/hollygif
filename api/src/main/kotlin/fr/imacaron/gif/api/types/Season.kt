package fr.imacaron.gif.api.types

import fr.imacaron.gif.shared.repository.SeasonEntity
import kotlinx.serialization.Serializable

@Serializable
data class Season(
	val id: Int,
	val number: Int,
	val series: Series
) {
	constructor(entity: SeasonEntity): this(
		entity.id,
		entity.number,
		Series(entity.series)
	)
}