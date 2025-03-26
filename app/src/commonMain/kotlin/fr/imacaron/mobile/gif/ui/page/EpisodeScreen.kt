package fr.imacaron.mobile.gif.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import fr.imacaron.mobile.gif.ui.EpisodeDetail
import fr.imacaron.mobile.gif.viewmodel.EpisodesViewModel
import kotlinx.coroutines.launch

@Composable
fun EpisodesScreen(seriesName: String, seasonNumber: Int, navController: NavHostController) {
	val viewModel = viewModel<EpisodesViewModel> { EpisodesViewModel(seriesName, seasonNumber) }
	val scope = rememberCoroutineScope()
	var isLoading by remember { mutableStateOf(false) }
	scope.launch {
		viewModel.fetch(0)
	}

	LazyColumn(
		Modifier.fillMaxWidth(),
		contentPadding = PaddingValues(horizontal = 16.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		itemsIndexed(viewModel.episodes.toList()) { index, episode ->
			if (index >= viewModel.episodes.size - 2 && !isLoading) {
				isLoading = true
				scope.launch {
					viewModel.nextPage()
					isLoading = false
				}
			}
			Card(modifier = Modifier.fillMaxWidth(), onClick = { navController.navigate(EpisodeDetail(
				seriesName, seasonNumber, episode.title, episode.number
			)) }) {
				Row(Modifier.padding(8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
					Text("Épisode ${episode.number}:", style = MaterialTheme.typography.titleLarge)
					Text("${episode.numberOfGif ?: 0} gif${if ((episode.numberOfGif ?: 0) > 1) "s" else ""} au total")
				}
				Row(Modifier.padding(8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
					Text(text = episode.title.replace('_', ' '), style = MaterialTheme.typography.titleMedium)
					Text("${(episode.duration / 60).toInt()}:${(episode.duration % 60).toInt()}")
				}
			}
		}
	}
}

