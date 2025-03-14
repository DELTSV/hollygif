package fr.imacaron.mobile.gif.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordUser(
    val id: String?,
    val username: String?,
    val discriminator: String?,
    @SerialName("global_name")
    val globalName: String?,
    val avatar: String?
)