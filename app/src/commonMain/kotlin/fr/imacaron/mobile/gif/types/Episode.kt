package fr.imacaron.mobile.gif.types

import kotlinx.serialization.Serializable

@Serializable
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
)