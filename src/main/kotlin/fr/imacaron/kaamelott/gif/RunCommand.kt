package fr.imacaron.kaamelott.gif

import java.io.File
import java.util.concurrent.TimeUnit

fun String.runCommand(
    workingDir: File = File("."),
    timeoutAmount: Long = 60,
    timeoutUnit: TimeUnit = TimeUnit.SECONDS
): String ? = runCatching {
    ProcessBuilder(this.splitCommand())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectErrorStream(true)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start().also { it.waitFor(timeoutAmount, timeoutUnit) }
        .inputStream.bufferedReader().readText()
}.onFailure { it.printStackTrace() }.getOrNull()

private fun String.splitCommand(): List<String> {
    val res = mutableListOf<String>()
    var s = StringBuilder()
    var inQuote = false
    var prevC: Char? = null
    for (c in this) {
        if(c == '\'' && prevC != '\\') {
            inQuote = !inQuote
        }
        if(c == ' ' && !inQuote) {
            res.add(s.toString())
            s = StringBuilder()
        } else {
            s.append(c)
        }
        prevC = c
    }
    res.add(s.toString())
    return res
}