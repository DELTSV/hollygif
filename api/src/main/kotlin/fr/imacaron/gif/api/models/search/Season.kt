package fr.imacaron.gif.api.models.search

import fr.imacaron.gif.shared.infrastrucutre.repository.SeasonEntity
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