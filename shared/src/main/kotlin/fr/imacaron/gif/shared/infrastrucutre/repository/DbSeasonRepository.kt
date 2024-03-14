package fr.imacaron.gif.shared.infrastrucutre.repository

import fr.imacaron.gif.shared.NotFoundException
import fr.imacaron.gif.shared.search.SeasonRepository
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.int

class DbSeasonRepository(
	private val db: Database
) : SeasonRepository {
	override fun getSeriesSeasons(series: SeriesEntity): Result<List<SeasonEntity>> =
		Result.success(db.seasons.filter { SeasonTable.series eq series.id }.map { it })

	override fun getSeriesSeason(series: SeriesEntity, number: Int): Result<SeasonEntity> =
		db.seasons.find { (SeasonTable.series eq series.id) and (SeasonTable.number eq number) }?.let {
			Result.success(it)
		} ?: Result.failure(NotFoundException("Season not found"))

	override fun getSeriesSeasonsSize(series: SeriesEntity): Int =
		db.seasons.count { SeasonTable.series eq series.id }

	override fun addSeriesSeason(series: String, season: SeasonEntity): Result<SeasonEntity> {
		val dbSeries = db.series.find { SeriesTable.name eq series } ?: return Result.failure(NotFoundException("Series not found"))
		season.series = dbSeries
		db.seasons.add(season)
		return Result.success(season)
	}
}

open class SeasonTable(alias: String?): Table<SeasonEntity>("SEASONS", alias) {
	companion object: SeasonTable(null)
	override fun aliased(alias: String) = SeasonTable(alias)

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

val Database.seasons get() = this.sequenceOf(SeasonTable)