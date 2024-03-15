package fr.imacaron.gif.shared.infrastrucutre

import java.io.File
import java.io.InputStream

class FileManager {
	fun fileExist(path: String): Boolean = File(path).exists()

	fun getFile(path: String): File = File(path)

	fun createFile(path: String, data: InputStream) {
		File(path).apply {
			createNewFile()
			writeBytes(data.readAllBytes())
		}
	}
}