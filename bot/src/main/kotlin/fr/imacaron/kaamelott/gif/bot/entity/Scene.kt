package fr.imacaron.kaamelott.gif.entity

import fr.imacaron.kaamelott.gif.ErrorWhileDrawingText
import fr.imacaron.kaamelott.gif.FFMPEG
import fr.imacaron.kaamelott.gif.NotEnoughTimeException
import fr.imacaron.kaamelott.gif.logger
import fr.imacaron.kaamelott.gif.repository.EpisodeEntity
import fr.imacaron.kaamelott.gif.repository.SceneEntity
import java.io.File

class Scene(
	val entity: SceneEntity,
	private val ep: EpisodeEntity
) {
	val start
		get() = entity.start

	val end
		get() = entity.end

	val index
		get() = entity.index

	val duration
		get() = end - start - (2.0 / ep.fps)

	fun createMeme(text: String, textSize: Int = 156): Result<String> {
		logger.debug("Create meme")
		if(duration < 0) {
			return Result.failure(NotEnoughTimeException())
		}
		val name = "${ep.season.number}${ep.number}$index$text".hashCode()
		val sceneName = "${ep.season.number}_${ep.number}_$index"
		val scene = "./out/$sceneName.mp4"
		val sceneFile = File(scene)
		if(!sceneFile.exists()) {
			FFMPEG.makeScene(
				"./episodes/L${ep.season.number}_E${ep.number.toString().padStart(3, '0')}.mkv",
				scene,
				start,
				end
			)
		}
		val textLength = FFMPEG.getTextLength(scene, text, textSize)
		if(textLength.isNaN()){
			return Result.failure(ErrorWhileDrawingText())
		}
		val avgChar = textLength / text.length
		val words = text.split(' ').filter { it.isNotBlank() }
		val lines = mutableListOf("")
		var index = 0
		words.forEach {  w ->
			if(lines[index].length * avgChar + w.length * avgChar + avgChar < ep.width) {
				lines[index] = "${lines[index]} $w"
			} else {
				lines.add(w)
				index++
			}
		}
		FFMPEG.writeText(scene, "./tmp/$name.mp4", lines, textSize)
		FFMPEG.convertToGif("./tmp/$name.mp4", "./gif/$name.gif", duration)
		logger.debug("Meme created")
		return Result.success("./gif/$name.gif")
	}
}