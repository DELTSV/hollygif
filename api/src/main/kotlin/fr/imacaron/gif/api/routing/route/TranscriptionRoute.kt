package fr.imacaron.gif.api.routing.route

import fr.imacaron.gif.api.respond
import fr.imacaron.gif.api.routing.resources.API
import fr.imacaron.gif.api.types.Response
import fr.imacaron.gif.api.types.Transcriptions
import fr.imacaron.gif.shared.entity.Episode
import fr.imacaron.gif.shared.entity.Transcription
import fr.imacaron.gif.shared.repository.EpisodeRepository
import fr.imacaron.gif.shared.repository.SeasonRepository
import fr.imacaron.gif.shared.repository.SeriesRepository
import fr.imacaron.gif.shared.repository.TranscriptionRepository
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get

class TranscriptionRoute(
    private val seriesRepository: SeriesRepository,
    private val seasonRepository: SeasonRepository,
    private val episodeRepository: EpisodeRepository,
    private val transcriptionRepository: TranscriptionRepository,
    application: Application
) {
    init {
        application.routing {
            getEpisodeTranscription()
        }
    }

    private fun Route.getEpisodeTranscription() {
        get<API.Series.Name.Seasons.Number.Episodes.Index.Transcriptions> {
            val series = seriesRepository.getSeries(it.episodeIndex.episodes.seasonNumber.seasons.seriesName.name).getOrElse {
                call.respond(Response.NotFound)
                return@get
            }
            val season = seasonRepository.getSeriesSeason(series, it.episodeIndex.episodes.seasonNumber.number).getOrElse {
                call.respond(Response.NotFound)
                return@get
            }
            val episode = episodeRepository.getSeasonEpisode(season, it.episodeIndex.index).getOrElse {
                call.respond(Response.NotFound)
                return@get
            }
            val transcriptions = transcriptionRepository.getEpisodeTranscriptions(episode).getOrElse {
                call.respond(Response.NotFound)
                return@get
            }
            call.respond(Response.Ok(transcriptions.map { t -> Transcriptions(t) }))
        }
    }
}