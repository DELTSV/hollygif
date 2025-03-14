package fr.imacaron.mobile.gif.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.imacaron.mobile.gif.ui.components.BottomBar
import fr.imacaron.mobile.gif.ui.page.GifList
import fr.imacaron.mobile.gif.ui.theme.AppTheme
import fr.imacaron.mobile.gif.viewmodel.LastGifViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
	AppTheme {
		var logged by remember { mutableStateOf(false) }
		val lastGifViewModel: LastGifViewModel = viewModel { LastGifViewModel() }
		val scope = rememberCoroutineScope()
		scope.launch {
			lastGifViewModel.fetch(0)
		}
		Scaffold(
			bottomBar = { BottomBar(logged) },
			contentWindowInsets = WindowInsets.statusBars
		) {
			Column(Modifier.fillMaxWidth().padding(it).padding(top = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
				GifList(lastGifViewModel.lastGifs.toList(), loadMore = { lastGifViewModel.nextPage() })
			}
		}
	}
}