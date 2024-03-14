package fr.imacaron.gif.api.usecases.search

import fr.imacaron.gif.api.models.search.Episode
import fr.imacaron.gif.shared.search.EpisodeRepository
import fr.imacaron.gif.shared.search.SeasonRepository
import fr.imacaron.gif.shared.search.SeriesRepository

class SearchEpisode(
        private val seriesRepository: SeriesRepository,
        private val seasonsRepository: SeasonRepository,
        private val episodeRepository: EpisodeRepository
) {
    operator fun invoke(series: String, season: Int, episode: Int): Result<Episode> {
        seriesRepository.getSeries(series).onSuccess { seriesEntity ->
            seasonsRepository.getSeriesSeason(seriesEntity, season).onSuccess { season ->
                episodeRepository.getSeasonEpisode(season, episode).onSuccess { episode ->
                    return Result.success(Episode(episode))
                }.onFailure { e ->
                    return Result.failure(e)
                }
            }.onFailure { e ->
                return Result.failure(e)
            }
        }.onFailure { e ->
            return Result.failure(e)
        }
        return Result.failure(Exception())
    }
}