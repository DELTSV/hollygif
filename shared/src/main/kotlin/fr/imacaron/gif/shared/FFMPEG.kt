package fr.imacaron.gif.shared

import java.io.File
import java.io.InputStream

object FFMPEG {
	fun getFileMetadata(path: String): Metadata {
		val fileName = path.split("/").last()
		val pathName = path.removeSuffix(fileName)
		val result =
			"ffmpeg -i $fileName -filter:v select='gt(scene,0.1)',showinfo -f null -".runCommand(workingDir = File(pathName))
				.apply { logger.debug(this) }?.lines()!!
		val (fps, width, height) = result.find { ".*Stream.*#[0-9]:[0-9]:.*Video.*".toRegex().matches(it) }!!
			.let { l ->
				val fr = "([0-9]+) fps".toRegex().find(l)?.groupValues?.get(1)!!.toInt()
				val (w, h) = "([0-9]+)x([0-9]+)".toRegex().find(l)!!.groupValues.let {
					it[1].toInt() to it[2].toInt()
				}
				Triple(fr, w, h)
			}
		val duration = result.find { ".*Duration: [0-9:]+.*".toRegex().matches(it) }!!.let { l ->
			"([0-9]+):([0-9]+):([0-9.]+)".toRegex().find(l)!!.groupValues.let {
				println(l)
				println(it)
				it[1].toInt() * 3600 + it[2].toInt() * 60 + it[3].toDouble()
			}
		}
		val timeCodes = result.filter { "pts_time" in it }.map {
			val start = it.indexOf("pts_time:") + 9
			val end = it.indexOf(' ', start)
			it.substring(start, end).toDouble()
		}.toMutableList()
		return Metadata(width, height, fps, duration, timeCodes)
	}

	fun makeScene(input: String, output: String, start: Double, end: Double) {
		val duration = end - start
		("ffmpeg -ss $start -i $input -t $duration -map 0:0 -map_chapters -1 -c copy $output").runCommand().apply {
			logger.debug(this)
		}
		logger.debug("Scene created")
	}

	fun makeSceneStream(input: String, start: Double, end: Double): InputStream? {
		val duration = end - start
		return ("ffmpeg -ss $start -i $input -t $duration -map_chapters -1 -c copy -f mpegts -").runCommandStream().apply {
			logger.debug("Scene created")
		}
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

	fun writeText(input: String, output: String, text: List<String>, textSize: Int = 156) {
		logger.debug("Writing text \"{}\" on scene {} with text size {}", text, input, textSize)
		var i = 1
		val files = mutableListOf<File>()
		logger.debug("Creating text files")
		val commands = text.reversed().map {
			val t = it.replace("'", "\\'")
			val path = "${input.removeSuffix(".mp4")}$i.txt"
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
		"ffmpeg -y -i $input -vf ${commands.joinToString(",")} $output".runCommand().apply {
			logger.debug(this)
		}
		logger.debug("Text written")
		logger.debug("Delete text files")
		files.forEach {
			logger.debug("	Deleting ${it.name}")
			it.delete()
		}
	}

	fun convertToGif(meme: String, duration: Double): InputStream? {
		logger.debug("Converting $meme to gif")
		return "ffmpeg -loglevel error -y -i $meme -r 15 -vf scale=512:-1,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse -ss 00:00:00 -to $duration -f gif -".runCommandStream().apply {
			logger.debug("Gif created")
		}
	}

	data class Metadata(
		val width: Int,
		val height: Int,
		val fps: Int,
		val duration: Double,
		val scenes: MutableList<Double>
	)
}