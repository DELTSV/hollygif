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
            val lines = f.readLines()
            val frameRate = lines.first().toInt()
            val width = lines[1].toInt()
            val height = lines[2].toInt()
            val duration = lines[3].toDouble()
            val timeCodes = lines.drop(4).map { it.toDouble() }
            EpisodeInfo(timeCodes, frameRate, width, height, duration)
        } else {
            val result =
                "ffmpeg -i episodes/$fileName -filter:v select='gt(scene,0.1)',showinfo -f null -".runCommand().apply { println(this) }?.lines()!!
            f.createNewFile()
            val (frameRate, width, height) = result.find { ".*Stream.*#[0-9]:[0-9]:.*Video.*".toRegex().matches(it) }!!.let { l ->
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
            EpisodeInfo(timeCodes, frameRate, width, height, duration)
        }
    }

    fun getSceneStart(index: Int) = if(index == 0) 0.0 else info.sceneChange[index - 1]

    fun getSceneDuration(index: Int) = if (index == 0) {
        info.sceneChange[0] - (1.0 / info.frameRate) * sqrt(info.frameRate.toDouble())
    } else {
        info.sceneChange[index] - info.sceneChange[index - 1] - (1.0 / info.frameRate) * sqrt(info.frameRate.toDouble())
    }

    fun createScene(start: Double, duration: Double): String {
        val path = "tmp/${UUID.randomUUID()}.mp4"
        println("ffmpeg -ss $start -i ./episodes/$fileName -t $duration -map 0:0 -map_chapters -1 -c copy $path".runCommand())
        return path
    }

    fun getTextLength(scene: String, text: String, textSize: Int = 156): Double {
        val t = text.replace("'", "")
        val lines ="ffmpeg -i $scene -vf drawtext=fontfile=./font.otf:fontsize=$textSize:text=\'$t\':x=0+0*print(tw):y=0+0*print(th) -vframes 1 -f null -".runCommand().apply { println(this) }!!.lines()
        try {
            return lines.first { "^[0-9.]+$".toRegex().matches(it) }.toDouble()
        } catch (e: NoSuchElementException) {
            return Double.NaN
        }
    }

    fun writeText(scene: String, text: List<String>, name: String, textSize: Int = 156) {
        var i = 1
        val files = mutableListOf<File>()
        val commands = text.reversed().map {
            val t = it.replace("'", "\\'")
            val path = "${scene.removeSuffix(".mp4")}$i.txt"
            files.add(File(path).apply {
                if(!exists()) {
                    this.createNewFile()
                }
                writeText(t)
            })
            "drawtext=fontfile=./font.otf:fontsize=$textSize:fontcolor=white:textfile=$path:x=(w-text_w)/2:y=h-(${i++}*th)-10"
        }
        println("ffmpeg -y -i $scene -vf ${commands.joinToString(",")} out/$name.mp4".runCommand())
        files.forEach { it.delete() }
    }

    fun convertToGif(meme: String): String {
        val name = meme.substring(meme.lastIndexOf('/')).removeSuffix(".mp4")
        "ffmpeg -y -i $meme -r 15 -vf scale=512:-1,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse -ss 00:00:00 -to 00:02:00 gif/$name.gif".runCommand()
        return "gif/$name.gif"
    }

    fun createMeme(name: String, sceneIndex: Int, text: String, textSize: Int = 156): String? {
        println("start = ${getSceneStart(sceneIndex)}, duration = ${getSceneDuration(sceneIndex)}")
        if(getSceneDuration(sceneIndex) < 0) {
            return null
        }
        val tmp = createScene(getSceneStart(sceneIndex), getSceneDuration(sceneIndex))
        println("scene $tmp created")
        val textLength = getTextLength(tmp, text, textSize)
        println("text length = $textLength")
        if(textLength.isNaN()) {
            return null
        }
        val avgChar = textLength / text.length
        val words = text.split(' ').filter { it.isNotBlank() }
        val lines = mutableListOf("")
        var index = 0
        words.forEach { w ->
            if(lines[index].length * avgChar + w.length * avgChar + avgChar < info.width) {
                lines[index] = "${lines[index]} $w"
            } else {
                lines.add(w)
                index++
            }
        }
        writeText(tmp, lines, name, textSize)
        println("text written")
        File(tmp).delete()
        val gif = convertToGif("out/$name.mp4")
        println("gif made")
        File("out/$name.mp4").delete()
        return gif
    }
}