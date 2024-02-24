package fr.imacaron.kaamelott.gif.entity

import fr.imacaron.kaamelott.gif.logger
import fr.imacaron.kaamelott.gif.repository.EpisodeRepository
import fr.imacaron.kaamelott.gif.repository.SeasonRepository
import fr.imacaron.kaamelott.gif.repository.SeriesEntity

class Series(
	private val seasonsRepository: SeasonRepository,
	private val episodeRepository: EpisodeRepository,
	val entity: SeriesEntity
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

	val seasons = SeasonList()

	inner class SeasonList {
		operator fun get(index: Int): Season {
			return seasonsRepository.getSeriesSeason(entity, index).getOrElse {
				throw IndexOutOfBoundsException()
			}
		}

		fun <K, V>associate(transform: (Season) -> Pair<K, V>): Map<K, V> {
			seasonsRepository.getSeriesSeasons(entity).onSuccess {  list ->
				return list.map { Season(episodeRepository, it) }.associate(transform)
			}.onFailure {
				logger.error("Cannot retrieve series ${entity.name} seasons", it)
			}
			return mapOf()
		}

		val size
			get() = seasonsRepository.getSeriesSeasonsSize(entity)
	}
}