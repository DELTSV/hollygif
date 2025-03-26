package fr.imacaron.mobile.gif.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import fr.imacaron.mobile.gif.Json
import fr.imacaron.mobile.gif.types.Gif
import fr.imacaron.mobile.gif.ui.GifView
import fr.imacaron.mobile.gif.ui.components.GifImage
import kotlinx.coroutines.launch

@Composable
fun GifList(data: List<Gif>, loadMore: suspend () -> Unit, navHostController: NavHostController) {
	var isLoading by remember { mutableStateOf(false) }
	val scope = rememberCoroutineScope()
	LazyColumn(
		modifier = Modifier.fillMaxSize(),
		contentPadding = PaddingValues(horizontal = 16.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		data.forEachIndexed { index, gif ->
			item {
				if(index == data.size - 2) {
					isLoading = true
					scope.launch {
						loadMore()
						isLoading = false
					}
				}
				Card(
					modifier = Modifier.fillMaxWidth(),
					colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
					elevation = CardDefaults.cardElevation(0.dp),
					onClick = { navHostController.navigate(GifView(Json.encodeToString(gif))) }
				) {
					Box {
						GifImage("https://gif.imacaron.fr/api/gif/file/${gif.file}", Modifier.fillMaxWidth())
						Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth().alpha(0.8f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp,
							Alignment.End)) {
							Text(gif.creator.globalName ?: "", style = MaterialTheme.typography.headlineSmall)
							if(gif.creator.avatar != null) {
								AsyncImage(model = gif.creator.avatar, null, Modifier.clip(CircleShape).width(24.dp))
							}
						}
					}
					Text("Épisode ${gif.scene.episode.title} Saison ${gif.scene.episode.season.number}", Modifier.padding(horizontal = 8.dp, vertical = 8.dp), style = MaterialTheme.typography.titleMedium)
				}
			}
		}
	}
}