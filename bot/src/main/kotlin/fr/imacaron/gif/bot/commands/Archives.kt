package fr.imacaron.gif.bot.commands

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.message.embed
import fr.imacaron.gif.shared.repository.GifRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class Archives(
	private val kord: Kord,
	private val gifRepository: GifRepository
) {
	suspend fun init(){
		kord.createGlobalChatInputCommand("archives", "Commande pour récupérer son historique d'utilisation du bot") {
			integer("page", "Page des archives")
		}
	}

	suspend operator fun invoke(interaction: GuildChatInputCommandInteraction) {
		val resp = interaction.deferEphemeralResponse()
		val page = interaction.command.integers["page"]?.toInt()?: 0
		val gifs = gifRepository.getUserGifs(interaction.user.id.toString(), page)
		resp.respond {
			embed {
				title = "Archives"
				if(gifs.isEmpty()) {
					description = "Les archives sont vides"
				}
				gifs.forEach { gif ->
					field {
						name = gif.date.toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
						value = "Livre: **${gif.season.number}** Episode: **${gif.episode.number}** Timecode **${gif.timeCode}** Texte **${gif.text}**"
					}
				}
			}
		}
	}
}