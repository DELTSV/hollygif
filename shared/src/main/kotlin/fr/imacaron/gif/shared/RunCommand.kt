package fr.imacaron.gif.shared

import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

fun String.runCommand(
    workingDir: File = File(".")
): String ? = runCatching {
    ProcessBuilder(this.splitCommand())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectErrorStream(true)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()
        .inputStream.bufferedReader().readText()
}.onFailure { it.printStackTrace() }.getOrNull()

fun String.runCommandStream(
    workingDir: File = File("."),
    commandInput: InputStream? = null
): InputStream? = kotlin.runCatching {
    ProcessBuilder(this.splitCommand())
        .directory(workingDir)
        .redirectInput(ProcessBuilder.Redirect.PIPE)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectErrorStream(true)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()
        .apply { commandInput?.transferTo(outputStream) }
        .inputStream
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