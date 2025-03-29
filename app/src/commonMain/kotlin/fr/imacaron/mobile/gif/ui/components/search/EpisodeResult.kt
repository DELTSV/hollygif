package fr.imacaron.mobile.gif.ui.components.search

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.imacaron.mobile.gif.types.Episode
import fr.imacaron.mobile.gif.ui.EpisodeDetail

fun LazyGridScope.EpisodeResult(data: List<Episode>, navController: NavHostController, loadMore: () -> Unit) {
	data.forEachIndexed { index, episode ->
		item(span = { GridItemSpan(3) }) {
			if(index == data.size - 1) {
				loadMore()
			}
			OutlinedCard(modifier = Modifier.padding(8.dp), onClick = { navController.navigate(EpisodeDetail(episode.season.series.name, episode.season.number, episode.title, episode.number)) }) {
				Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
					Text("Épisode ${episode.number}: ")
					Text(episode.title.replace("_", " "), overflow = TextOverflow.Ellipsis, maxLines = 1, fontWeight = FontWeight.Bold)
				}
			}
		}
	}
}