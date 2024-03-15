package fr.imacaron.gif.api.usecases.gif

import fr.imacaron.gif.shared.CannotUploadToBucket
import fr.imacaron.gif.shared.ErrorWhileDrawingText
import fr.imacaron.gif.shared.NotEnoughTimeException
import fr.imacaron.gif.shared.gif.SceneEntity
import fr.imacaron.gif.shared.gif.SceneRepository
import fr.imacaron.gif.shared.gif.Status
import fr.imacaron.gif.shared.infrastrucutre.FFMPEG
import fr.imacaron.gif.shared.infrastrucutre.FileManager
import fr.imacaron.gif.shared.logger
import fr.imacaron.gif.shared.search.EpisodeRepository
import fr.imacaron.gif.shared.search.SeasonRepository
import fr.imacaron.gif.shared.search.SeriesRepository
import io.ktor.server.plugins.*
import io.ktor.utils.io.charsets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class CreateGif(
	private val fileManager: FileManager,
	private val seriesRepository: SeriesRepository,
	private val seasonRepository: SeasonRepository,
	private val episodeRepository: EpisodeRepository,
	private val sceneRepository: SceneRepository
) {
	operator fun invoke(gifCreation: GifCreation, textSize: Int): Result<String> {
		val series = seriesRepository.getSeries(gifCreation.series).getOrElse {
			return Result.failure(NotFoundException())
		}
		val season = seasonRepository.getSeriesSeason(series, gifCreation.season).getOrElse {
			return Result.failure(NotFoundException())
		}
		val episode = episodeRepository.getSeasonEpisode(season, gifCreation.episode).getOrElse {
			return Result.failure(NotFoundException())
		}
		val time = try {
			gifCreation.timecode.split(":").let {
				if (it.size != 2 || it[1].length != 2) {
					return Result.failure(MalformedInputException("Invalid timecode"))
				}
				it[0].toInt() * 60 + it[1].toDouble()
			}
		} catch (e: NumberFormatException) {
			logger.debug("Time code not only numbers")
			return Result.failure(Exception())
		}
		val scene = sceneRepository.getEpisodeSceneAt(episode, time).getOrElse {
			return Result.failure(NotFoundException())
		}
		val duration = scene.end - scene.start
		logger.debug("Create meme")
		if(duration < 0) {
			return Result.failure(NotEnoughTimeException())
		}
		val name = "${season.number}_${episode.number}_${scene.index}_${gifCreation.text.hashCode()}"
		val sceneName = "${season.number}_${episode.number}_${scene.index}"
		val scenePath = "./out/$sceneName.mp4"
		if(!fileManager.fileExist(scenePath)) {
			FFMPEG.makeScene(
				"./episodes/L${season.number}_E${episode.number.toString().padStart(3, '0')}.mkv",
				scenePath,
				scene.start,
				scene.end
			)
		}
		val textLength = FFMPEG.getTextLength(scenePath, gifCreation.text, textSize)
		if(textLength.isNaN()){
			return Result.failure(ErrorWhileDrawingText())
		}
		val avgChar = textLength / gifCreation.text.length
		val words = gifCreation.text.split(' ').filter { it.isNotBlank() }
		val lines = mutableListOf("")
		var index = 0
		words.forEach {  w ->
			if(lines[index].length * avgChar + w.length * avgChar + avgChar < episode.width) {
				lines[index] = "${lines[index]} $w"
			} else {
				lines.add(w)
				index++
			}
		}
		FFMPEG.writeText(scenePath, "./tmp/$name.mp4", lines, textSize)
		val gif = FFMPEG.convertToGif("./tmp/$name.mp4", duration)
		if (gif != null) {
			logger.debug("Meme created")
			fileManager.createFile("./gif/$name.gif", gif)
			return Result.success("$name.gif")
		} else {
			return Result.failure(Exception())
		}
	}
}