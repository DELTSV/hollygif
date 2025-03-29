package fr.imacaron.mobile.gif.types

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a GIF with metadata and associated information about its creation and context.
 *
 * @property id The unique identifier of the GIF.
 * @property creator The [DiscordUser] who created the GIF.
 * @property file The filename of the GIF file.
 * @property createdAt The timestamp when the GIF was created.
 * @property scene The [Scene] associated with the GIF.
 * @property timecode The specific timecode in the scene where the GIF is extracted (format mm:ss[.mmm]).
 * @property text The caption associated with the GIF.
 */
@Serializable
@SerialName("gif")
data class Gif(
    val id: Int,
    val creator: DiscordUser,
    val file: String,
    val createdAt: Instant,
    val scene: Scene,
    val timecode: String,
    val text: String
): Searchable {
    companion object {
        val type = "gif"
    }
}