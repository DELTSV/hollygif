package fr.imacaron.mobile.gif.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import fr.imacaron.mobile.gif.Json
import fr.imacaron.mobile.gif.types.Episode
import fr.imacaron.mobile.gif.types.Gif
import fr.imacaron.mobile.gif.ui.GifView
import fr.imacaron.mobile.gif.ui.components.GifImage
import fr.imacaron.mobile.gif.viewmodel.EpisodeDetailViewModel
import fr.imacaron.mobile.gif.viewmodel.EpisodesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeDetailScreen(seriesName: String, seasonNumber: Int, episodeNumber: Int, navController: NavHostController) {
	val episodesViewModel = viewModel<EpisodesViewModel> { EpisodesViewModel(seriesName, seasonNumber) }
	var episode: Episode? by remember { mutableStateOf(null) }
	val scope = rememberCoroutineScope()
	LaunchedEffect(episodesViewModel, episodeNumber) {
		episode = episodesViewModel.getEpisode(episodeNumber)
	}
	episode?.let { ep ->
		val episodeDetailViewModel = viewModel<EpisodeDetailViewModel> { EpisodeDetailViewModel(ep) }
		LaunchedEffect(episodesViewModel, ep) {
			episodeDetailViewModel.fetchGif(0)
			episodeDetailViewModel.fetchTranscriptions()
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
					Text("${(ep.duration / 60).toInt()}:${(ep.duration % 60).toInt()}")
				}
			}
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
					HorizontalUncontainedCarousel(
						state = carouselState,
						itemWidth = width - 100.dp,
						itemSpacing = 4.dp,
						flingBehavior = CarouselDefaults.singleAdvanceFlingBehavior(carouselState)
					) {
						if(it == episodeDetailViewModel.gifs.size - 2) {
							scope.launch {
								episodeDetailViewModel.nextGifPage()
							}
						}
						if(it < episodeDetailViewModel.gifs.size) {
							val gif = episodeDetailViewModel.gifs.toList()[it]
							Box {
								GifImage("https://gif.imacaron.fr/api/gif/file/${gif.file}", Modifier.clip(RoundedCornerShape(8.dp)).clickable {
									navController.navigate(GifView(Json.encodeToString(gif)))
								})
								Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth().alpha(0.8f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp,
									Alignment.Start)) {
									Text(gif.creator.globalName ?: "", style = MaterialTheme.typography.titleSmall)
									if(gif.creator.avatar != null) {
										AsyncImage(model = gif.creator.avatar, null, Modifier.clip(CircleShape).width(16.dp))
									}
								}
							}
						}
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