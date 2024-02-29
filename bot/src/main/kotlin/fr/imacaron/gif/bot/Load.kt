package fr.imacaron.gif.bot

import fr.imacaron.gif.shared.FFMPEG
import fr.imacaron.gif.shared.entity.Series
import fr.imacaron.gif.shared.repository.*
import java.io.File


class Loader(
	private val sceneRepository: SceneRepository,
	private val episodeRepository: EpisodeRepository,
	private val seasonRepository: SeasonRepository,
	private val seriesRepository: SeriesRepository
) {

	lateinit var series: Series

	fun loadSeries(name: String) {
		seriesRepository.getSeries(name).onSuccess {
			series = Series(seasonRepository, episodeRepository, sceneRepository, it)
			return
		}
		seriesRepository.addSeries(SeriesEntity{
			this.name = name
		}).onSuccess {
			series = Series(seasonRepository, episodeRepository, sceneRepository, it)
		}.onFailure {
			logger.error("Cannot load series $name", it)
		}
	}

	fun loadSeason(number: Int) {
		for(i in 1..number) {
			if(seasonRepository.getSeriesSeason(series.entity, i).isFailure) {
				seasonRepository.addSeriesSeason(series.name, SeasonEntity {
					this.number = i
					this.series = this@Loader.series.entity
				}).onFailure {
					logger.error("Cannot load season $i in series ${series.name}", it)
				}
			}
		}
	}

	fun loadEpisodesInSeason(
		episodeFormat: String,
		seasonNumber: Int,
		dir: File
	) {
		if(!dir.isDirectory) {
			logger.error("File ${dir.absolutePath} is not a directory")
			return
		}
		val season = series.seasons[seasonNumber]
		dir.listFiles()?.forEach { epFile ->
			val result = Regex(episodeFormat).find(epFile.name) ?: return@forEach
			val number = result.groupValues[1].toInt()
			if(episodeRepository.getSeasonEpisode(season.entity, number).isSuccess) {
				return@forEach
			}
			val title = result.groupValues[2].replace("_", " ")
			val metadata = FFMPEG.getFileMetadata(epFile.absolutePath)
			episodeRepository.addEpisode(EpisodeEntity {
				this.number = number
				this.title = title
				this.season = season.entity
				this.width = metadata.width
				this.height = metadata.height
				this.duration = metadata.duration
				this.fps = metadata.fps
			}).onSuccess {
				metadata.scenes.add(0, .0)
				metadata.scenes.forEachIndexed { index, d ->
					sceneRepository.addEpisodeScene(SceneEntity {
						this.start = d
						this.end = metadata.scenes.getOrNull(index + 1) ?: metadata.duration
						this.index = index
						this.episode = it
					})
				}
			}
		}
	}
}
