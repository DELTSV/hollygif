package fr.imacaron.gif.shared.search

import fr.imacaron.gif.shared.infrastrucutre.repository.SeriesEntity

interface SeriesRepository {
    fun getSeries(): Result<List<SeriesEntity>>

    fun getSeries(name: String): Result<SeriesEntity>

    fun addSeries(series: SeriesEntity): Result<SeriesEntity>
}