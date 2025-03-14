package fr.imacaron.mobile.gif.types

import kotlinx.serialization.Serializable

@Serializable
data class Scene(
    val id: Int,
    val index: Int,
    val start: Double,
    val end: Double,
    val episode: Episode
)