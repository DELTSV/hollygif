package fr.imacaron.kaamelott.gif

import java.io.File
import java.util.UUID
import kotlin.math.sqrt

class Episode(
	val number: Byte,
	val book: Byte
) {
	private val fileName = "L${book}_E${number.toString().padStart(3, '0')}.mkv"
	private val infoFileName = "L${book}_E${number.toString().padStart(3, '0')}.info"

	val info: EpisodeInfo by lazy {
		val f = File("./info/$infoFileName")
		if (f.exists()) {
			logger.debug("File for ep $number book $book already exist. Parsing it")
			val lines = f.readLines()
			val frameRate = lines.first().toInt()
			val width = lines[1].toInt()
			val height = lines[2].toInt()
			val duration = lines[3].toDouble()
			val timeCodes = lines.drop(4).map { it.toDouble() }
			logger.debug("Parsed info")
			logger.debug("	frameRate: $frameRate")
			logger.debug("	width: $width")
			logger.debug("	height: $height")
			logger.debug("	duration: $duration")
			logger.debug("	timeCodes: {}", timeCodes)
			EpisodeInfo(timeCodes, frameRate, width, height, duration)
		} else {
			logger.debug("no info found")
			logger.info("Collecting info for ep $number book $book")
			val result =
				"ffmpeg -i episodes/$fileName -filter:v select='gt(scene,0.1)',showinfo -f null -".runCommand()
					.apply { logger.debug(this) }?.lines()!!
			f.createNewFile()
			val (frameRate, width, height) = result.find { ".*Stream.*#[0-9]:[0-9]:.*Video.*".toRegex().matches(it) }!!
				.let { l ->
					val fr = "([0-9]+) fps".toRegex().find(l)?.groupValues?.get(1)!!.toInt()
					val (w, h) = "([0-9]+)x([0-9]+)".toRegex().find(l)!!.groupValues.let {
						it[1].toInt() to it[2].toInt()
					}
					f.writeText("$fr\n$w\n$h\n")
					Triple(fr, w, h)
				}
			val duration = result.find { ".*Duration: [0-9:]+.*".toRegex().matches(it) }!!.let { l ->
				"([0-9]+):([0-9]+):([0-9.]+)".toRegex().find(l)!!.groupValues.let {
					println(l)
					println(it)
					it[1].toInt() * 3600 + it[2].toInt() * 60 + it[3].toDouble()
				}
			}.apply { f.appendText("$this\n") }
			val timeCodes = result.let { lines ->
				lines.filter { "pts_time" in it }.map {
					val start = it.indexOf("pts_time:") + 9
					val end = it.indexOf(' ', start)
					it.substring(start, end).toDouble()
				}
			}.apply {
				f.appendText(this.joinToString(separator = "\n"))
			}
			logger.debug("Collected info:")
			logger.debug("	frameRate: $frameRate")
			logger.debug("	width: $width")
			logger.debug("	height: $height")
			logger.debug("	duration: $duration")
			logger.debug("	timeCodes: {}", timeCodes)
			EpisodeInfo(timeCodes, frameRate, width, height, duration)
		}
	}

	fun getSceneStart(index: Int): Double = if (index == 0) {
		0.0
	} else {
		info.sceneChange[index - 1]
	}.also { logger.debug("Get scene $index start: $it") }

	fun getSceneDuration(index: Int): Double = if (index == 0) {
		info.sceneChange[0] - (1.0 / info.frameRate) * sqrt(info.frameRate.toDouble())
	} else {
		info.sceneChange[index] - info.sceneChange[index - 1] - (1.0 / info.frameRate) * sqrt(info.frameRate.toDouble())
	}.also { logger.debug("Get scene $index duration: $it") }

	fun createScene(start: Double, duration: Double): String {
		val path = "tmp/${UUID.randomUUID()}.mp4"
		logger.debug("Creating scene at $path")
		"ffmpeg -ss $start -i ./episodes/$fileName -t $duration -map 0:0 -map_chapters -1 -c copy $path".runCommand().apply {
			logger.debug(this)
		}
		logger.debug("Scene created")
		return path
	}

	fun getTextLength(scene: String, text: String, textSize: Int = 156): Double {
		logger.debug("Getting text \"$text\" length with text size: $textSize")
		val t = text.replace("'", "")
		val lines =
			"ffmpeg -i $scene -vf drawtext=fontfile=./font.otf:fontsize=$textSize:text=\'$t\':x=0+0*print(tw):y=0+0*print(th) -vframes 1 -f null -".runCommand()
				.apply {
					logger.debug(this)
				}!!.lines()
		return try {
			lines.first { "^[0-9.]+$".toRegex().matches(it) }.toDouble().apply {
				logger.debug("Text \"$text\" with text size $textSize length: $this")
			}
		} catch (e: NoSuchElementException) {
			logger.debug("Text length: Nan")
			logger.warn("Error getting text \"$text\" length, getting NaN")
			Double.NaN
		}
	}

	fun writeText(scene: String, text: List<String>, name: String, textSize: Int = 156) {
		logger.debug("Writing text \"{}\" on scene {} with text size {}", text, scene, textSize)
		var i = 1
		val files = mutableListOf<File>()
		logger.debug("Creating text files")
		val commands = text.reversed().map {
			val t = it.replace("'", "\\'")
			val path = "${scene.removeSuffix(".mp4")}$i.txt"
			logger.debug("	File: $path\n	Text: $t")
			files.add(File(path).apply {
				if (!exists()) {
					logger.debug("		Creating file")
					this.createNewFile()
				}
				writeText(t)
				logger.debug("		Done")
			})
			"drawtext=fontfile=./font.otf:fontsize=$textSize:fontcolor=white:textfile=$path:x=(w-text_w)/2:y=h-(${(i++*textSize)})-10"
		}
		logger.debug("Drawtext commands: {}", commands)
		"ffmpeg -y -i $scene -vf ${commands.joinToString(",")} out/$name.mp4".runCommand().apply {
			logger.debug(this)
		}
		logger.debug("Text written")
		logger.debug("Delete text files")
		files.forEach {
			logger.debug("	Deleting ${it.name}")
			it.delete()
		}
	}

	fun convertToGif(meme: String): String {
		logger.debug("Converting $meme to gif")
		val name = meme.substring(meme.lastIndexOf('/')).removeSuffix(".mp4")
		"ffmpeg -y -i $meme -r 15 -vf scale=512:-1,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse -ss 00:00:00 -to 00:02:00 gif/$name.gif".runCommand().apply {
			logger.debug(this)
		}
		logger.debug("Gif 'gif/$name.gif' created")
		return "gif/$name.gif"
	}

	fun createMeme(name: String, sceneIndex: Int, text: String, textSize: Int = 156): Result<String> {
		logger.debug("Creating meme")
		if (getSceneDuration(sceneIndex) < 0) {
			return Result.failure(NotEnoughTimeException())
		}
		val tmp = createScene(getSceneStart(sceneIndex), getSceneDuration(sceneIndex))
		val textLength = getTextLength(tmp, text, textSize)
		if (textLength.isNaN()) {
			return Result.failure(ErrorWhileDrawingText())
		}
		val avgChar = textLength / text.length
		val words = text.split(' ').filter { it.isNotBlank() }
		val lines = mutableListOf("")
		var index = 0
		words.forEach { w ->
			if (lines[index].length * avgChar + w.length * avgChar + avgChar < info.width) {
				lines[index] = "${lines[index]} $w"
			} else {
				lines.add(w)
				index++
			}
		}
		writeText(tmp, lines, name, textSize)
		File(tmp).delete()
		val gif = convertToGif("out/$name.mp4")
		File("out/$name.mp4").delete()
		logger.debug("Meme created")
		return Result.success(gif)
	}
}