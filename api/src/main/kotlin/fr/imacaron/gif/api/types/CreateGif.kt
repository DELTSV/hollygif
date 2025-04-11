package fr.imacaron.gif.api.types

import kotlinx.serialization.Serializable

@Serializable
data class CreateGif(
	val scene: Scene,
	val text: String,
	val textSize: Int? = null
)
