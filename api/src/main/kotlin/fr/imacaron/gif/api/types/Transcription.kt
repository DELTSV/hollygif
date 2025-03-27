package fr.imacaron.gif.api.types

import fr.imacaron.gif.shared.repository.TranscriptionEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("transcription")
data class Transcription(
    val index: Int,
    val episode: Episode,
    val text: String,
    val info: String?,
    val speaker: String
): Searchable {
    constructor(entity: TranscriptionEntity): this(
        entity.index,
        Episode(entity.episode),
        entity.text,
        entity.info,
        entity.speaker
    )

    companion object {
        val type = "transcription"
    }
}
