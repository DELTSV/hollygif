package fr.imacaron.gif.shared.repository

import fr.imacaron.gif.shared.CannotCreate
import fr.imacaron.gif.shared.NotFoundException
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.like
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

class SeriesRepository(
	private val db: Database
) {
	fun getSeries(): Result<List<SeriesEntity>> = Result.success(db.series.toList())

	fun getSeries(name: String): Result<SeriesEntity> = db.series.find { SeriesTable.name eq name }?.let {
		Result.success(it)
	} ?: Result.failure(NotFoundException("Series not found"))

	fun addSeries(series: SeriesEntity): Result<SeriesEntity>{
		return if(db.series.add(series) == 1) {
			Result.success(series)
		} else {
			Result.failure(CannotCreate("Cannot create series"))
		}
	}

	fun searchSeriesByName(search: String, page: Int, pageSize: Int): Result<Pair<List<SeriesEntity>, Int>> {
		val total = db.series.count { it.name like search }
		val series = db.series.filter { it.name like search }.drop(page * pageSize).take(pageSize).map { it }
		return Result.success(series to total)
	}
}

object SeriesTable: Table<SeriesEntity>("SERIES") {
	val id = int("id_series").primaryKey().bindTo { it.id }
	val name = varchar("name").bindTo { it.name }
	val logo = varchar("logo").bindTo { it.logo }
}

interface SeriesEntity: Entity<SeriesEntity> {
	var id: Int
	var name: String
	var logo: String

	companion object: Entity.Factory<SeriesEntity>()
}

val Database.series get() = this.sequenceOf(SeriesTable)