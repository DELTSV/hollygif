package fr.imacaron.mobile.gif.types

import kotlinx.serialization.Serializable

@Serializable
data class SceneStatus(
	val scene: Boolean = false,
	val textLength: Boolean = false,
	val text: Boolean = false,
	val gif: Boolean = false,
	val gifId: Int? = null,
	val error: String? = null
)
