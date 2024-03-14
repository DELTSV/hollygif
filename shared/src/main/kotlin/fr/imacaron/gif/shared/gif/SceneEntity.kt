package fr.imacaron.gif.shared.gif

import fr.imacaron.gif.shared.*
import fr.imacaron.gif.shared.search.Episode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File

class SceneEntity(
	val entity: DbSceneEntity,
	private val ep: Episode
) {
	val start
		get() = entity.start

	val end
		get() = entity.end

	val index
		get() = entity.index

	val duration
		get() = end - start - (2.0 / ep.fps)

	data class Status(
		val scene: Boolean = false,
		val textLength: Boolean = false,
		val text: Boolean = false,
		val gif: Boolean = false,
		val result: String? = null,
		val error: Exception? = null
	)

	fun createMeme(text: String, textSize: Int = 156): Flow<Status> = flow {
		this@SceneEntity.ep.season.series.s3
		logger.debug("Create meme")
		if(duration < 0) {
			emit(Status(error = NotEnoughTimeException()))
			return@flow
		}
		val name = "${ep.season.number}_${ep.number}_${index}_${text.hashCode()}"
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
		emit(Status(scene = true))
		val textLength = FFMPEG.getTextLength(scene, text, textSize)
		if(textLength.isNaN()){
			emit(Status(scene = true, error = ErrorWhileDrawingText()))
			return@flow
		} else {
			emit(Status(scene = true, textLength = true))
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
		emit(Status(scene = true, textLength = true, text = true))
		val gif = FFMPEG.convertToGif("./tmp/$name.mp4", duration)
		if (gif != null) {
			emit(Status(scene = true, textLength = true, text = true, gif = true))
			logger.debug("Meme created")
			withContext(Dispatchers.IO) {
				this@SceneEntity.ep.season.series.s3.putFile("gif", "$name.gif", gif.readAllBytes())
				logger.debug("Meme uploaded")
			}
			emit(Status(scene = true, textLength = true, text = true, gif = true, result = "$name.gif"))
		} else {
			emit(Status(scene = true, textLength = true, text = true, error = CannotUploadToBucket()))
		}
	}
}