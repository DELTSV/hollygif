package fr.imacaron.kaamelott.gif.entity

import fr.imacaron.kaamelott.gif.repository.EpisodeRepository
import fr.imacaron.kaamelott.gif.repository.SeasonRepository
import fr.imacaron.kaamelott.gif.repository.SeriesEntity

class Series(
	private val seasonsRepository: SeasonRepository,
	private val episodeRepository: EpisodeRepository,
	private val entity: SeriesEntity
) {
	var id
		get() = entity.id
		set(value) {
			entity.id = value
			entity.flushChanges()
		}
	var name
		get() = entity.name
		set(value) {
			entity.name = value
			entity.flushChanges()
		}

	val seasons: List<Season> by lazy {
		seasonsRepository.getSeriesSeasons(entity).getOrElse {
			return@lazy listOf()
		}.map { Season(episodeRepository, it) }
	}
}