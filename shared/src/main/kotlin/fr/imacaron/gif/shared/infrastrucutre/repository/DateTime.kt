package fr.imacaron.gif.shared.infrastrucutre.repository

import kotlinx.datetime.*
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.SqlType
import java.sql.*

fun BaseTable<*>.datetime(name: String): Column<Instant> {
	return registerColumn(name, InstantDateTimeSqlType)
}

object InstantDateTimeSqlType : SqlType<Instant>(Types.TIMESTAMP, "datetime") {

	override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: Instant) {
		ps.setTimestamp(index, Timestamp.from(parameter.toJavaInstant()))
	}

	override fun doGetResult(rs: ResultSet, index: Int): Instant? {
		return rs.getTimestamp(index)?.toInstant()?.toKotlinInstant()
	}
}

fun BaseTable<*>.date(name: String): Column<LocalDate> {
	return registerColumn(name, LocalDateSqlType)
}

object LocalDateSqlType : SqlType<LocalDate>(Types.DATE, "date") {

	override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: LocalDate) {
		ps.setDate(index, Date.valueOf(parameter.toJavaLocalDate()))
	}

	override fun doGetResult(rs: ResultSet, index: Int): LocalDate? {
		return rs.getDate(index)?.toLocalDate()?.toKotlinLocalDate()
	}
}