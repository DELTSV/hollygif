package fr.imacaron.mobile.gif.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TimeText(time: Number, modifier: Modifier = Modifier, ms: Boolean = false) {
	val result = when(time) {
		is Int -> "${(time / 60).toInt()}:${(time%60).toString().padStart(2, '0')}"
		is Double -> "${(time / 60).toInt()}:${(time%60).toInt().toString().padStart(2, '0')}${if(ms) ":${time-time.toInt()}" else ""}"
		else -> ""
	}
	Text(result, modifier)
}