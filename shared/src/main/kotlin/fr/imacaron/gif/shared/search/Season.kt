package fr.imacaron.gif.shared.search

import fr.imacaron.gif.shared.infrastrucutre.repository.DbEpisodeRepository
import fr.imacaron.gif.shared.infrastrucutre.repository.DbSceneRepository
import fr.imacaron.gif.shared.infrastrucutre.repository.SeasonEntity

class Season(
		private val episodeRepository: DbEpisodeRepository,
		private val sceneRepository: DbSceneRepository,
		val entity: SeasonEntity,
		val series: Series
) {
	var id
		get() = entity.id
		set(value) {
			entity.id = value
			entity.flushChanges()
		}

	var number
		get() = entity.number
		set(value) {
			entity.number = value
			entity.flushChanges()
		}

	val episodes: EpisodeList = EpisodeList()

	inner class EpisodeList {
		operator fun get(i: Int): Episode {
			return episodeRepository.getSeasonEpisode(entity, i).getOrElse {
				throw IndexOutOfBoundsException()
			}.let { Episode(sceneRepository, it, this@Season) }
		}

		val size by lazy {
			episodeRepository.getSeasonEpisodesCount(entity).getOrElse { 0 }
		}
	}
}