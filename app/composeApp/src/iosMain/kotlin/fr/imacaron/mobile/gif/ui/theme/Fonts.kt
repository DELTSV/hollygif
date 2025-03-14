package fr.imacaron.mobile.gif.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.readResourceBytes

private val cache = mutableMapOf<String, Font>()

@OptIn(InternalResourceApi::class)
@Composable
actual fun font(name: String, res: String, weight: FontWeight, style: FontStyle): Font {
	return cache.getOrPut(res) {
		val byteArray = runBlocking {
			readResourceBytes("font/$res.ttf")
		}
		Font(res, byteArray, weight, style)
	}
}