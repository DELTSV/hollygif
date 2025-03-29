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
import fr.imacaron.mobile.gif.types.Transcription
import fr.imacaron.mobile.gif.ui.EpisodeDetail

fun LazyGridScope.TextResult(data: List<Transcription>, navController: NavHostController, loadMore: () -> Unit) {
	data.forEachIndexed { index, text ->
		if(index == data.size - 1) {
			loadMore()
		}
		item(span = { GridItemSpan(3)}) {
			OutlinedCard(modifier = Modifier.padding(8.dp), onClick = { navController.navigate(EpisodeDetail(text.episode.season.series.name, text.episode.season.number, text.episode.title, text.episode.number)) }) {
				Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
					Text("${text.speaker}: ", fontWeight = FontWeight.Bold)
					Text(text.text, overflow = TextOverflow.Ellipsis, maxLines = 1)
				}
			}
		}
	}
}