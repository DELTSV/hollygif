package fr.imacaron.gif.bot

import com.mchange.v2.c3p0.ComboPooledDataSource
import dev.kord.common.entity.Choice
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.value
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.behavior.interaction.suggest
import dev.kord.core.event.interaction.AutoCompleteInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.addFile
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.KtorRequestException
import fr.imacaron.gif.shared.ErrorWhileDrawingText
import fr.imacaron.gif.shared.NotEnoughTimeException
import fr.imacaron.gif.bot.commands.Archives
import fr.imacaron.gif.shared.repository.*
import io.ktor.util.logging.*
import kotlinx.datetime.Clock
import org.ktorm.database.Database
import org.ktorm.support.mysql.MySqlDialect
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*
import kotlin.io.path.Path

val logger: Logger = LoggerFactory.getLogger("fr.imacaron.kaamelott.gif.bot")

val API = System.getenv("API")

suspend fun main(args: Array<String>) {
    val token = System.getenv("TOKEN") ?: run {
        logger.error("No TOKEN specified")
        return
    }

    val cpds = ComboPooledDataSource().apply {
        driverClass = "org.mariadb.jdbc.Driver"
        jdbcUrl = System.getenv("DB_URL")
        user = System.getenv("DB_USER")
        password = System.getenv("DB_PASSWORD")
        minPoolSize = 5
        acquireIncrement = 5
        maxPoolSize = 10
    }

    val db = Database.connect(cpds, dialect = MySqlDialect())

    val gifRepository = GifRepository(db)
    val sceneRepository = SceneRepository(db)
    val episodeRepository = EpisodeRepository(db, sceneRepository)
    val seasonRepository = SeasonRepository(db, episodeRepository)
    val seriesRepository = SeriesRepository(db, seasonRepository, episodeRepository)

    if(args.size > 1) {
        val loader = Loader(sceneRepository, episodeRepository, seasonRepository, seriesRepository)
        loader.loadSeries(args[0])
        loader.loadSeason(args[1].toInt())
        for(i in 1..loader.series.seasons.size) {
            val dir = File("${args[2]}$i")
            loader.loadEpisodesInSeason("Kaamelott\\.S[0-9]{2}E([0-9]+)\\.(.*)\\.mkv$", i, dir)
        }
        return
    }

    val kaamelott = seriesRepository.getSeries("kaamelott").getOrElse {
        logger.error("Missing kaamelott")
        return
    }

    val kord = Kord(token)

    val archives = Archives(kord, gifRepository).apply { init() }

    val episodeNumbers = kaamelott.seasons.associate {
        it.number to it.episodes.size
    }

    if(episodeNumbers.size != 6) {
        logger.error("Missing book")
        return
    }

    kord.createGlobalChatInputCommand("kaagif", "Une commande pour créer des gif kaamelott") {
        integer("livre", "Livre") {
            required = true
            for (i in episodeNumbers.keys) {
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
        string("text", "Texte")
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
        when(interaction.command.rootName) {
            "archives" -> archives(interaction)
            "kaagif" -> {
                val user = interaction.user
                logger.debug("Receive command from ${user.effectiveName}. Defer it")
                val resp = interaction.deferPublicResponse()
                val name = UUID.randomUUID()
                try {
                    val epNum = interaction.command.integers["episode"]?.toInt() ?: run {
                        logger.debug("No ep num in command")
                        resp.respondBadCommand(user)
                        return@on
                    }
                    val book = interaction.command.integers["livre"]?.toInt() ?: run {
                        logger.debug("No book in command")
                        resp.respondBadCommand(user)
                        return@on
                    }
                    if (book <= 0 || book > kaamelott.seasons.size) {
                        resp.respondBookError(user)
                        return@on
                    }
                    val season = kaamelott.seasons[book]
                    if (epNum <= 0 || book > season.episodes.size) {
                        resp.respondEpNumError(user)
                        return@on
                    }
                    val timecode = interaction.command.strings["timecode"] ?: run {
                        logger.debug("No timecode in command")
                        resp.respondBadCommand(user)
                        return@on
                    }
                    val ep = season.episodes[epNum]
                    val time = try {
                        timecode.split(":").let {
                            if (it.size != 2) {
                                resp.respondTimecode(user)
                                return@on
                            }
                            it[0].toInt() * 60 + it[1].toInt()
                        }
                    } catch (e: NumberFormatException) {
                        logger.debug("Time code not only numbers")
                        resp.respondTimecode(user)
                        return@on
                    }
                    if (time > ep.duration) {
                        logger.debug("Timecode greater than episode duration")
                        resp.respondTropLoin(ep.duration, time, user)
                        return@on
                    }
                    if (time < 0) {
                        logger.debug("Timecode less than 0")
                        resp.respondTropCourt(user)
                        return@on
                    }
                    val text = interaction.command.strings["text"] ?: run {
                        logger.debug("No text in command")
                        ""
                    }
                    val scene = ep.scenes.getSceneFromTime(time.toDouble()) ?: run {
                        logger.debug("Scene doesn't exist")
                        resp.respondNoScene(user, timecode)
                        return@on
                    }
                    logger.debug("Getting scene {}, starting at {} and last {}", scene, scene.start, scene.duration)
                    logger.debug("Creating meme")
                    scene.createMeme(text)
                        .onFailure {
                            logger.debug("Creating meme failed", it)
                            when (it) {
                                is NotEnoughTimeException -> {
                                    logger.debug("Not enough time to create scene")
                                    resp.respondPortionTropCourte(user)
                                }

                                is ErrorWhileDrawingText -> {
                                    logger.debug("Error while drawing text on scene")
                                    resp.respondTexteErreur(user)
                                }
                            }
                        }
                        .onSuccess {
                            logger.debug("meme $it successfully created")
                            gifRepository.addGif(GifEntity {
                                this.scene = scene.entity
                                this.date = Clock.System.now()
                                this.text = text
                                this.user = user.id.toString()
                                this.timecode = timecode
                            })
                            resp.respond {
                                embed {
                                    title = "Gif créer"
                                    author {
                                        this.name = user.effectiveName
                                        val avatar = user.memberAvatar?.cdnUrl?.toUrl() ?:
                                        user.avatar?.cdnUrl?.toUrl() ?:
                                        if(user.discriminator == "0") {
                                            "https://discord.com/api/v10/embed/avatars/${user.id.toString().toLong().shr(22) % 6}.png"
                                        } else {
                                            "https://discord.com/api/v10/embed/avatars/${user.discriminator.toInt() % 5}.png"
                                        }
                                        this.icon = avatar
                                    }
                                    this.field {
                                        this.name = "Livre"
                                        this.value = book.toString()
                                        this.inline = true
                                    }
                                    this.field {
                                        this.name = "Épisode"
                                        this.value = epNum.toString()
                                        this.inline = true
                                    }
                                    this.field {
                                        this.name = "Time Code"
                                        this.value = timecode
                                        this.inline = true
                                    }
                                    if (text.isNotBlank()) {
                                        this.field {
                                            this.name = "Texte"
                                            this.value = text
                                        }
                                    }
                                    logger.debug("IMAGE URL = $API/api/gif/$it")
                                    image = "$API/api/gif/$it"
                                    url = "$API/api/gif/$it"
                                }
                            }
                        }
                } catch (e: Exception) {
                    when (e) {
                        is KtorRequestException -> {
                            if (e.status.code == 413) {
                                val size = File("gif/$name.gif").length()
                                logger.warn("File $name.gif too large, ${size}B")
                                resp.repondTropGros(user, size, "$name.gif")
                            } else {
                                logger.error(e)
                                resp.respondUnknownError(user)
                            }
                        }

                        else -> {
                            logger.error(e)
                            resp.respondUnknownError(user)
                        }
                    }
                }
            }
        }
    }

    logger.info("Starting")
    kord.login()
    logger.info("Gracefully stopping")
}