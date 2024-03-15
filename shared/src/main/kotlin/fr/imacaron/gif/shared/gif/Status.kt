package fr.imacaron.gif.shared.gif

data class Status(
	val scene: Boolean = false,
	val textLength: Boolean = false,
	val text: Boolean = false,
	val gif: Boolean = false,
	val result: String? = null,
	val error: Exception? = null
)