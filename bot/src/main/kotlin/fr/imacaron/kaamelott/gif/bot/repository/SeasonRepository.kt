package fr.imacaron.kaamelott.gif.repository

import fr.imacaron.kaamelott.gif.NotFoundException
import fr.imacaron.kaamelott.gif.entity.Season
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.int

class SeasonRepository(
	private val db: Database,
	private val episodeRepository: EpisodeRepository
) {
	fun getSeriesSeasons(series: SeriesEntity): Result<List<SeasonEntity>> =
		Result.success(db.seasons.filter { it.series eq series.id }.map { it })

	fun getSeriesSeason(series: SeriesEntity, number: Int): Result<Season> =
		db.seasons.find { (it.series eq series.id) and (it.number eq number) }?.let {
			Result.success(Season(episodeRepository, it))
		} ?: Result.failure(NotFoundException("Season not found"))

	fun getSeriesSeasonsSize(series: SeriesEntity): Int =
		db.seasons.count { it.series eq series.id }

	fun addSeriesSeason(series: String, season: SeasonEntity): Result<SeasonEntity> {
		val dbSeries = db.series.find { it.name eq series } ?: return Result.failure(NotFoundException("Series not found"))
		season.series = dbSeries
		db.seasons.add(season)
		return Result.success(season)
	}
}

object SeasonTable: Table<SeasonEntity>("SEASONS") {
	val id = int("id_season").primaryKey().bindTo { it.id }
	val number = int("number").bindTo { it.number }
	val series = int("series").references(SeriesTable) { it.series }
}

interface SeasonEntity: Entity<SeasonEntity> {
	var id: Int
	var number: Int
	var series: SeriesEntity

	companion object: Entity.Factory<SeasonEntity>()
}

internal val Database.seasons get() = this.sequenceOf(SeasonTable)