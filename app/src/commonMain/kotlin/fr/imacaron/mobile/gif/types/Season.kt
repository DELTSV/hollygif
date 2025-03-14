package fr.imacaron.mobile.gif.types

import kotlinx.serialization.Serializable

@Serializable
data class Season(
    val id: Int,
    val number: Int,
    val series: Series
)