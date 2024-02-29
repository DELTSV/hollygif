package fr.imacaron.gif.shared.entity

import fr.imacaron.gif.shared.repository.EpisodeRepository
import fr.imacaron.gif.shared.repository.SceneRepository
import fr.imacaron.gif.shared.repository.SeasonEntity

class Season(
	private val episodeRepository: EpisodeRepository,
	private val sceneRepository: SceneRepository,
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