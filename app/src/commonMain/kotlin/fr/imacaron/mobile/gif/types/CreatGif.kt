package fr.imacaron.mobile.gif.types

import kotlinx.serialization.Serializable

@Serializable
data class CreateGif(
	val scene: Scene,
	val text: String,
	val textSize: Int? = null
)