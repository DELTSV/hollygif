package fr.imacaron.gif.api.types

import fr.imacaron.gif.shared.repository.TranscriptionEntity
import kotlinx.serialization.Serializable

@Serializable
data class Transcriptions(
    val index: Int,
    val episode: Episode,
    val text: String,
    val info: String?,
    val speaker: String
) {
    constructor(entity: TranscriptionEntity): this(
        entity.index,
        Episode(entity.episode),
        entity.text,
        entity.info,
        entity.speaker
    )
}
