package fr.imacaron.gif.shared.search

import fr.imacaron.gif.shared.infrastrucutre.repository.EpisodeEntity
import fr.imacaron.gif.shared.infrastrucutre.repository.SeasonEntity

interface EpisodeRepository {
    fun getSeasonEpisodes(season: SeasonEntity, page: Int, pageSize: Int): Result<List<EpisodeEntity>>

    fun getSeasonEpisodesCount(season: SeasonEntity): Result<Int>

    fun getSeasonEpisode(season: SeasonEntity, number: Int): Result<EpisodeEntity>

    fun addEpisode(episode: EpisodeEntity): Result<EpisodeEntity>
}