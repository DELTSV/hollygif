package fr.imacaron.gif.shared.infrastrucutre.enums

import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.SqlType
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

class EnumIntType<C : TEnum<Int>>(private val enumClass: Class<C>) : SqlType<C>(Types.INTEGER, "integer") {

	/**
	 * The method to get the instance of [C] enum with an [Int] value
	 * @author Denis TURBIEZ
	 */
	private val method = enumClass.getDeclaredMethod("getValue", Int::class.java).also { it.isAccessible = true }

	/**
	 * @author Denis TURBIEZ
	 */
	override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: C) {
		ps.setInt(index, parameter.value)
	}

	/**
	 * @author Denis TURBIEZ
	 */
	override fun doGetResult(rs: ResultSet, index: Int): C? {
		return rs.getInt(index).let { enumClass.cast(method(enumClass.enumConstants.first(), it)) }
	}
}

/**
 * The function to use in [BaseTable] declaration to have an enum [C] that will be stored as [Int]
 * @author Denis TURBIEZ
 * @see fr.kamae.api.bdd.Users
 * @param name The name of the field in the database
 */
inline fun <reified C : TEnum<Int>> BaseTable<*>.enumInt(name: String): Column<C> {
	return registerColumn(name, EnumIntType(C::class.java))
}