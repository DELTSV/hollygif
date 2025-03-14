package fr.imacaron.mobile.gif.types

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Gif(
    val id: Int,
    val creator: DiscordUser,
    val file: String,
    val createdAt: Instant,
    val scene: Scene,
    val timecode: String,
    val text: String
)