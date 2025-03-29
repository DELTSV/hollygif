package fr.imacaron.mobile.gif.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("series")
data class Series(
    val id: Int,
    val name: String,
    val logo: String
): Searchable {
    companion object {
        val type = "series"
    }
}