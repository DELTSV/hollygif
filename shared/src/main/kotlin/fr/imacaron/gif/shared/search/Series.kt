package fr.imacaron.gif.shared.search

import fr.imacaron.gif.shared.infrastrucutre.S3File
import fr.imacaron.gif.shared.infrastrucutre.repository.DbEpisodeRepository
import fr.imacaron.gif.shared.logger
import fr.imacaron.gif.shared.infrastrucutre.repository.DbSceneRepository
import fr.imacaron.gif.shared.infrastrucutre.repository.DbSeasonRepository
import fr.imacaron.gif.shared.infrastrucutre.repository.SeriesEntity

class Series(
		private val seasonsRepository: DbSeasonRepository,
		private val episodeRepository: DbEpisodeRepository,
		private val sceneRepository: DbSceneRepository,
		val entity: SeriesEntity
) {

	val s3 = S3File(
		System.getenv("S3_ACCESS_KEY"),
		System.getenv("S3_SECRET_KEY"),
		System.getenv("S3_URL"),
		System.getenv("S3_REGION"),
		entity.name
	)

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
			}.let { Season(episodeRepository, sceneRepository, it, this@Series) }
		}

		fun <K, V>associate(transform: (Season) -> Pair<K, V>): Map<K, V> {
			seasonsRepository.getSeriesSeasons(entity).onSuccess {  list ->
				return list.map { Season(episodeRepository, sceneRepository, it, this@Series) }.associate(transform)
			}.onFailure {
				logger.error("Cannot retrieve series ${entity.name} seasons", it)
			}
			return mapOf()
		}

		val size
			get() = seasonsRepository.getSeriesSeasonsSize(entity)
	}
}