package fr.imacaron.mobile.gif.types

import kotlinx.serialization.Serializable

@Serializable
data class Transcription(
	val index: Int,
	val info: String?,
	val speaker: String,
	val text: String,
	val episode: Episode
)
