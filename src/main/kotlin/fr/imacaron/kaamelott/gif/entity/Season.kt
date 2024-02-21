package fr.imacaron.kaamelott.gif.entity

import fr.imacaron.kaamelott.gif.repository.EpisodeRepository
import fr.imacaron.kaamelott.gif.repository.SeasonEntity

class Season(
	private val episodeRepository: EpisodeRepository,
	val entity: SeasonEntity
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
		operator fun get(i: Int): Result<Episode> {
			return episodeRepository.getSeasonEpisode(entity, i)
		}

		val size by lazy {
			episodeRepository.getSeasonEpisodesCount(entity).getOrElse { 0 }
		}
	}
}