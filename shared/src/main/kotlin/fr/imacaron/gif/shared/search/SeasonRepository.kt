package fr.imacaron.gif.shared.search

import fr.imacaron.gif.shared.infrastrucutre.repository.SeasonEntity
import fr.imacaron.gif.shared.infrastrucutre.repository.SeriesEntity

interface SeasonRepository {
    fun getSeriesSeasons(series: SeriesEntity): Result<List<SeasonEntity>>
    fun getSeriesSeason(series: SeriesEntity, number: Int): Result<SeasonEntity>
    fun getSeriesSeasonsSize(series: SeriesEntity): Int
    fun addSeriesSeason(series: String, season: SeasonEntity): Result<SeasonEntity>
}