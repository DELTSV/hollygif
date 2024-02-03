package fr.imacaron.kaamelott.gif

import dev.kord.common.entity.Choice
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.value
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.DeferredPublicMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.behavior.interaction.suggest
import dev.kord.core.event.Event
import dev.kord.core.event.interaction.AutoCompleteInteractionCreateEvent
import dev.kord.core.event.interaction.GlobalChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildApplicationCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.addFile
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import io.ktor.client.request.forms.*
import io.ktor.util.logging.*
import org.slf4j.simple.SimpleLoggerFactory
import java.io.File
import java.util.*
import kotlin.io.path.Path
import kotlin.time.Duration.Companion.seconds

suspend fun main() {
    val logger = SimpleLoggerFactory().getLogger("Main logger")
    val token = System.getenv("TOKEN") ?: run {
        logger.error("No TOKEN specified")
        return
    }

    val kord = Kord(token)

    val episodeNumbers = mutableMapOf<Int, Int>()
    File("episodes").apply {
        list()?.forEach { f ->
            episodeNumbers[f[1].digitToInt()] = (episodeNumbers[f[1].digitToInt()] ?: 0) + 1
        }
    }

    kord.createGlobalChatInputCommand("kaagif", "Une commande pour créer des gif kaamelot") {
        integer("livre", "Livre") {
            required = true
            for (i in 1..6) {
                choice("Livre $i", i.toLong())
            }
        }
        integer("episode", "Épisode") {
            required = true
            autocomplete = true
        }
        string("timecode", "Timecode sous la forme mm:ss") {
            required = true
        }
        string("text", "Texte") {
            required = true
        }
    }

    kord.on<AutoCompleteInteractionCreateEvent> {
        val options = interaction.command.data.options.value ?: return@on
        when(interaction.command.data.name.value) {
            "kaagif" -> {
                if(options[1].value.value?.focused.value == true) {
                    val ep = interaction.command.strings["episode"]
                    val choices = interaction.command.integers["livre"]?.let { livre ->
                        (1..episodeNumbers[livre.toInt()]!!).asSequence().map {
                            Choice.IntegerChoice("Épisode $it", Optional(null), it.toLong())
                        }.filter { (ep ?: "") in it.value.toString() }
                    } ?: (1..25).asSequence().map {
                        Choice.IntegerChoice("Épisode $it", Optional(null), it.toLong())
                    }
                    interaction.suggest(choices.take(25).toList())
                }
            }
        }
    }

    kord.on<GuildChatInputCommandInteractionCreateEvent> {
        val resp = interaction.deferPublicResponse()
        try {
            val ep = Episode(
                interaction.command.integers["episode"]!!.toByte(),
                interaction.command.integers["livre"]!!.toByte()
            )
            val time = try {
                interaction.command.strings["timecode"]!!.split(":").let {
                    if (it.size != 2) {
                        resp.respondTimecode()
                        return@on
                    }
                    it[0].toInt() * 60 + it[1].toInt()
                }
            } catch (e: NumberFormatException) {
                resp.respondTimecode()
                return@on
            }
            if(time > ep.info.duration) {
                resp.respondTropLoin(ep.info.duration, time)
                return@on
            }
            if(time < 0) {
                resp.respondTropCourt()
                return@on
            }
            val text = interaction.command.strings["text"]!!
            val scene = (ep.info.sceneChange.indexOfFirst { it > time } - 1).coerceAtLeast(0) + 1
            val gif = ep.createMeme("${UUID.randomUUID()}", scene, text)
            if (gif == null) {
                resp.respond {
                    content = "Erreur lors de la création du gif"
                }
            } else {
                resp.respond {
                    addFile(Path(gif))
                }
            }
        }catch (e: Exception) {
            logger.error(e)
            resp.respondUnknownError()
        }
    }

    kord.login()
}

suspend fun DeferredPublicMessageInteractionResponseBehavior.respondTimecode() {
    respond {
        content = "Le time code doit être sous la forme `mm:ss`"
        addFile(Path("pas_compliquer.gif"))
    }
}

suspend fun DeferredPublicMessageInteractionResponseBehavior.respondTropLoin(duration: Double, demand: Int) {
    respond {
        val min = duration.seconds.inWholeMinutes
        content = "L'épisode fait ${min}min${duration.toInt()%60}s donc la scène à la ${demand/60}min et ${demand%60}s. Bon voilà quoi"
        addFile(Path("pas_compliquer.gif"))
    }
}

suspend fun DeferredPublicMessageInteractionResponseBehavior.respondTropCourt() {
    respond {
        content = "Euh l'épisode il commence à 00:00 pas avant"
        addFile(Path("pas_compliquer.gif"))
    }
}


suspend fun DeferredPublicMessageInteractionResponseBehavior.respondUnknownError() {
    respond {
        content = "Une erreur est survenue"
    }
}