package fr.imacaron.mobile.gif.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import fr.imacaron.mobile.gif.types.Gif
import fr.imacaron.mobile.gif.ui.components.GifImage

@Composable
fun GifList(data: List<Gif>, loadMore: suspend () -> Unit) {
	val state = rememberLazyListState()
	val bottom: Boolean by remember {
		derivedStateOf {
			val lastItemVisible = state.layoutInfo.visibleItemsInfo.lastOrNull()
			lastItemVisible?.index != 0 && lastItemVisible?.index == state.layoutInfo.totalItemsCount - 2
		}
	}
	LaunchedEffect(bottom) {
		if(bottom) {
			loadMore()
		}
	}
	LazyColumn(
		modifier = Modifier.fillMaxSize(),
		contentPadding = PaddingValues(horizontal = 16.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
		state = state
	) {
		data.forEach { gif ->
			item {
				Card(
					modifier = Modifier.fillMaxWidth(),
					colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
					elevation = CardDefaults.cardElevation(0.dp),
					onClick = { println("Clicked $gif") }
				) {
					GifImage("https://gif.imacaron.fr/api/gif/file/${gif.file}", Modifier.fillMaxWidth())
					Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
						if(gif.creator.avatar != null) {
							AsyncImage(model = gif.creator.avatar, null, Modifier.clip(CircleShape).width(24.dp))
						}
						Text(gif.creator.globalName ?: "", style = MaterialTheme.typography.headlineSmall)
					}
					Text("Épisode ${gif.scene.episode.title} Saison ${gif.scene.episode.season.number}", Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.titleLarge)
				}
			}
		}
	}
}