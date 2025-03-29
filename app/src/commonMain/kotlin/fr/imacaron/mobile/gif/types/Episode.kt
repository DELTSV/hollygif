package fr.imacaron.mobile.gif.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("episode")
data class Episode(
    val id: Int,
    val number: Int,
    val width: Int,
    val height: Int,
    val fps: Int,
    val title: String,
    val season: Season,
    val duration: Double,
    val numberOfGif: Int? = null
): Searchable {
    companion object {
        val type = "episode"
    }
}