package fr.imacaron.mobile.gif.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("transcription")
data class Transcription(
	val index: Int,
	val info: String?,
	val speaker: String,
	val text: String,
	val episode: Episode
): Searchable {
	companion object {
		val type = "transcription"
	}
}
