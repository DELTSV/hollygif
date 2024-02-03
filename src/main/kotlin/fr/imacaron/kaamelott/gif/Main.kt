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
import kotlin.math.log
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
        } ?: run {
            logger.error("episodes files doesn't contain anything")
            return
        }
    }
    if(episodeNumbers.size != 6) {
        logger.error("Missing book")
        return
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
                    val ep = interaction.command.strings["episode"] ?: run {
                        logger.debug("No episode in command")
                        return@on
                    }
                    val choices = interaction.command.integers["livre"]?.let { livre ->
                        val number = episodeNumbers[livre.toInt()] ?: run {
                            logger.debug("Missing book in episodeNumbers")
                            return@on
                        }
                        (1..number).asSequence().map {
                            logger.debug("Choice(\"Épisode $it\", Optional(null), $it)")
                            Choice.IntegerChoice("Épisode $it", Optional(null), it.toLong())
                        }.filter {
                            (ep in it.value.toString()).apply {
                                logger.debug("${it.value} is keep with ep=$ep: $this")
                            }
                        }
                    } ?: (1..25).asSequence().map {
                        logger.debug("Choice(\"Épisode $it\", Optional(null), $it)")
                        Choice.IntegerChoice("Épisode $it", Optional(null), it.toLong())
                    }
                    interaction.suggest(choices.take(25).toList())
                }
            }
        }
    }

    kord.on<GuildChatInputCommandInteractionCreateEvent> {
        val user = interaction.user
        logger.debug("Receive command from ${user.effectiveName}. Defer it")
        val resp = interaction.deferPublicResponse()
        try {
            val epNum = interaction.command.integers["episode"]?.toByte() ?: run {
                logger.debug("No ep num in command")
                return@on
            }
            val book = interaction.command.integers["livre"]?.toByte() ?: run {
                logger.debug("No book in command")
                return@on
            }
            val ep = Episode(epNum, book)
            val time = try {
                interaction.command.strings["timecode"]?.split(":")?.let {
                    if (it.size != 2) {
                        resp.respondTimecode()
                        return@on
                    }
                    it[0].toInt() * 60 + it[1].toInt()
                } ?: run {
                    logger.debug("No timecode in command")
                    return@on
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
            val text = interaction.command.strings["text"] ?: run {
                logger.debug("No text in command")
                return@on
            }
            val scene = (ep.info.sceneChange.indexOfFirst { it > time } - 1).coerceAtLeast(0) + 1
            logger.debug("Getting scene $scene, starting at ${ep.getSceneStart(scene)} and last ${ep.getSceneDuration(scene)}")
            logger.debug("Creating meme")
            ep.createMeme("${UUID.randomUUID()}", scene, text)
                .onFailure {
                    logger.debug("Creating meme failed", it)
                    when(it) {
                        is NotEnoughTimeException -> {
                            logger.debug("Not enough time to create scene")
                            resp.respond {
                                content = "La portion choisie est trop petite, veuillez en sélectionner une autre"
                            }
                        }
                        is ErrorWhileDrawingText -> {
                            logger.debug("Error while drawing text on scene")
                            resp.respond {
                                content = "Erreur lors du dessin du texte"
                            }
                        }
                    }
                }
                .onSuccess {
                    logger.debug("meme $it successfully created")
                    resp.respond {
                        addFile(Path(it))
                    }
                }
        }catch (e: Exception) {
            logger.error(e)
            resp.respondUnknownError()
        }
    }

    logger.info("Starting")
    kord.login()
    logger.info("Gracefully stopping")
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