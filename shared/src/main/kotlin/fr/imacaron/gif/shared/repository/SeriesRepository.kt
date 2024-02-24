package fr.imacaron.gif.shared.repository

import fr.imacaron.gif.shared.CannotCreate
import fr.imacaron.gif.shared.NotFoundException
import fr.imacaron.gif.shared.entity.Series
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

class SeriesRepository(
	private val db: Database,
	private val seasonRepository: SeasonRepository,
	private val episodeRepository: EpisodeRepository
) {
	fun getSeries(): Result<List<Series>> = Result.success(db.series.map { Series(seasonRepository, episodeRepository, it) })

	fun getSeries(name: String): Result<Series> = db.series.find { SeriesTable.name eq name }?.let {
		Result.success(Series(seasonRepository, episodeRepository, it))
	} ?: Result.failure(NotFoundException("Series not found"))

	fun addSeries(series: SeriesEntity): Result<SeriesEntity>{
		return if(db.series.add(series) == 1) {
			Result.success(series)
		} else {
			Result.failure(CannotCreate("Cannot create series"))
		}
	}
}

object SeriesTable: Table<SeriesEntity>("SERIES") {
	val id = int("id_series").primaryKey().bindTo { it.id }
	val name = varchar("name").bindTo { it.name }
}

interface SeriesEntity: Entity<SeriesEntity> {
	var id: Int
	var name: String

	companion object: Entity.Factory<SeriesEntity>()
}

val Database.series get() = this.sequenceOf(SeriesTable)