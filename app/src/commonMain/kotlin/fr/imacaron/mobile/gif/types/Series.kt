package fr.imacaron.mobile.gif.types

import kotlinx.serialization.Serializable

@Serializable
data class Series(
    val id: Int,
    val name: String,
    val logo: String
)