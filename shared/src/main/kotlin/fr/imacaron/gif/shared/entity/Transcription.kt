package fr.imacaron.gif.shared.entity

import fr.imacaron.gif.shared.repository.TranscriptionEntity

class Transcription(
    private val entity: TranscriptionEntity,
    val episode: Episode
) {
    val index: Int
        get() = entity.index

    val text: String
        get() = entity.text

    val info: String?
        get() = entity.info

    val speaker: String
        get() = entity.speaker
}