package fr.imacaron.kaamelott.gif

import java.io.File

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
		val timeCodes = result.let { lines ->
			lines.filter { "pts_time" in it }.map {
				val start = it.indexOf("pts_time:") + 9
				val end = it.indexOf(' ', start)
				it.substring(start, end).toDouble()
			}.toMutableList()
		}
		return Metadata(width, height, fps, duration, timeCodes)
	}

	data class Metadata(
		val width: Int,
		val height: Int,
		val fps: Int,
		val duration: Double,
		val scenes: MutableList<Double>
	)
}