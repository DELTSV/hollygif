package fr.imacaron.gif.bot

import com.mchange.v2.c3p0.ComboPooledDataSource
import dev.kord.core.Kord
import dev.kord.core.event.interaction.AutoCompleteInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import fr.imacaron.gif.bot.commands.Archives
import fr.imacaron.gif.bot.commands.GifCommand
import fr.imacaron.gif.shared.entity.Series
import fr.imacaron.gif.shared.repository.*
import org.ktorm.database.Database
import org.ktorm.support.mysql.MySqlDialect
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

val logger: Logger = LoggerFactory.getLogger("fr.imacaron.kaamelott.gif.bot")

val API: String = System.getenv("API")
val APP: String = System.getenv("APP")

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
        maxIdleTime = 28_800
        idleConnectionTestPeriod = 14400
    }

    val db = Database.connect(cpds, dialect = MySqlDialect())

    val gifRepository = GifRepository(db)
    val sceneRepository = SceneRepository(db)
    val episodeRepository = EpisodeRepository(db)
    val seasonRepository = SeasonRepository(db)
    val seriesRepository = SeriesRepository(db)

    if(args.size > 1 && args[0] != "LOAD") {
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
    }.let {
        Series(seasonRepository, episodeRepository, sceneRepository, it)
    }

    val kord = Kord(token)

    val episodeNumbers = kaamelott.seasons.associate {
        it.number to it.episodes.size
    }

    val archives = Archives(kord, gifRepository).apply { init() }
    val gifCommand = GifCommand(kord, episodeNumbers, kaamelott, gifRepository).apply { init() }


    if(episodeNumbers.size != 6) {
        logger.error("Missing book")
        return
    }

    kord.on<AutoCompleteInteractionCreateEvent> {
        when(interaction.command.data.name.value) {
            "kaagif" -> gifCommand.autoCompleteEpisode(interaction)
        }
    }

    kord.on<GuildChatInputCommandInteractionCreateEvent> {
        when(interaction.command.rootName) {
            "archives" -> archives(interaction)
            "kaagif" -> gifCommand(interaction)
        }
    }

    logger.info("Starting")
    kord.login()
    logger.info("Gracefully stopping")
}