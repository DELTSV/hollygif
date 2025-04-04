package fr.imacaron.mobile.gif.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselDefaults
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import fr.imacaron.mobile.gif.Json
import fr.imacaron.mobile.gif.types.Episode
import fr.imacaron.mobile.gif.ui.GifView
import fr.imacaron.mobile.gif.ui.components.GifContainer
import fr.imacaron.mobile.gif.ui.components.PlayerCard
import fr.imacaron.mobile.gif.ui.components.TimeText
import fr.imacaron.mobile.gif.viewmodel.EpisodeDetailViewModel
import fr.imacaron.mobile.gif.viewmodel.EpisodesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeDetailScreen(seriesName: String, seasonNumber: Int, episodeNumber: Int, navController: NavHostController, pref: DataStore<Preferences>) {
	val episodesViewModel = viewModel<EpisodesViewModel> { EpisodesViewModel(seriesName, seasonNumber) }
	var episode: Episode? by remember { mutableStateOf(null) }
	val scope = rememberCoroutineScope()
	LaunchedEffect(episodesViewModel, episodeNumber) {
		episode = episodesViewModel.getEpisode(episodeNumber)
	}
	episode?.let { ep ->
		val episodeDetailViewModel = viewModel<EpisodeDetailViewModel> { EpisodeDetailViewModel(ep, pref) }
		LaunchedEffect(episodesViewModel, ep) {
			episodeDetailViewModel.fetchGif(0)
			episodeDetailViewModel.fetchTranscriptions()
			episodeDetailViewModel.fetchScenes()
		}
		LaunchedEffect(episodeDetailViewModel.gifId) {
			episodeDetailViewModel.gifId?.let { gifId ->
				episodeDetailViewModel.getGif(gifId)?.let { gif ->
					episodeDetailViewModel.gifId = null
					navController.navigate(GifView(Json.encodeToString(gif)))
				}
			}
		}
		Column(
			Modifier.padding(horizontal = 16.dp).fillMaxWidth().verticalScroll(rememberScrollState()),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Card(modifier = Modifier.fillMaxWidth()) {
				Row(Modifier.padding(8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
					Text("Épisode ${episodeNumber}:", style = MaterialTheme.typography.titleLarge)
					Text("${ep.numberOfGif ?: 0} gif${if ((ep.numberOfGif ?: 0) > 1) "s" else ""} au total")
				}
				Row(Modifier.padding(8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
					Text(text = ep.title.replace('_', ' '), style = MaterialTheme.typography.titleMedium)
					TimeText(ep.duration)
				}
			}
			PlayerCard(episodeDetailViewModel.scenes, episodeDetailViewModel.currentScene, { scene ->
				episodeDetailViewModel.scenes.indexOfFirst { it == scene }.let { index ->
					if(index != -1) {
						episodeDetailViewModel.currentScene = index
					}
				} }, {
					scope.launch(Dispatchers.IO) { episodeDetailViewModel.createGif(it) }
				}, episodeDetailViewModel.status)
			if (episodeDetailViewModel.gifs.isNotEmpty()) {
				val carouselState = rememberCarouselState {
					return@rememberCarouselState ep.numberOfGif ?: 0
				}
				var width by remember { mutableStateOf(0.dp) }
				val density = LocalDensity.current
				Card(
					Modifier.onGloballyPositioned {
						width = with(density) {
							it.size.width.toDp()
						}
					}
				) {
					if(episodeDetailViewModel.gifs.size > 1) {
						HorizontalUncontainedCarousel(
							state = carouselState,
							itemWidth = width - 100.dp,
							itemSpacing = 4.dp,
							flingBehavior = CarouselDefaults.singleAdvanceFlingBehavior(carouselState)
						) {
							if (it == episodeDetailViewModel.gifs.size - 2) {
								scope.launch {
									episodeDetailViewModel.nextGifPage()
								}
							}
							if (it < episodeDetailViewModel.gifs.size) {
								val gif = episodeDetailViewModel.gifs.toList()[it]
								GifContainer(gif, navController)
							}
						}
					} else {
						val gif = episodeDetailViewModel.gifs.toList()[0]
						GifContainer(gif, navController)
					}
				}
			}
			Card(Modifier.padding(8.dp)) {
				episodeDetailViewModel.transcriptions.forEach {
					Row {
						Text(it.speaker, fontWeight = FontWeight.Bold)
						if(it.info?.isNotEmpty() == true) {
							Text("(${it.info})")
						}
						Text(": ${it.text}")
					}
				}
			}
		}
	}
}