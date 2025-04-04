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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.toRoute
import fr.imacaron.mobile.gif.Json
import fr.imacaron.mobile.gif.TOKEN
import fr.imacaron.mobile.gif.types.Gif
import fr.imacaron.mobile.gif.ui.components.BottomBar
import fr.imacaron.mobile.gif.ui.components.TopBar
import fr.imacaron.mobile.gif.ui.page.EpisodeDetailScreen
import fr.imacaron.mobile.gif.ui.page.EpisodesScreen
import fr.imacaron.mobile.gif.ui.page.GifList
import fr.imacaron.mobile.gif.ui.page.GifViewScreen
import fr.imacaron.mobile.gif.ui.page.SearchScreen
import fr.imacaron.mobile.gif.ui.page.SeasonsScreen
import fr.imacaron.mobile.gif.ui.page.SeriesScreen
import fr.imacaron.mobile.gif.ui.theme.AppTheme
import fr.imacaron.mobile.gif.viewmodel.DiscordViewModel
import fr.imacaron.mobile.gif.viewmodel.LastGifViewModel
import fr.imacaron.mobile.gif.viewmodel.MyGifViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Serializable
data class GifView(val gif: String)

@Serializable
object Home

@Serializable
object Series

@Serializable
data class Seasons(val seriesName: String)

@Serializable
data class Episodes(val seriesName: String, val seasonNumber: Int)

@Serializable
data class EpisodeDetail(val seriesName: String, val seasonNumber: Int, val episodeName: String, val episodeNumber: Int)

@Serializable
object MyGif

@Serializable
object Search

@Composable
@Preview
fun App(pref: DataStore<Preferences>, navController: NavHostController = rememberNavController()) {
	AppTheme {
		var logged by remember { mutableStateOf(false) }
		val lastGifViewModel: LastGifViewModel = viewModel { LastGifViewModel() }
		val myGifViewModel: MyGifViewModel = viewModel { MyGifViewModel(pref) }
		val discordViewModel: DiscordViewModel = viewModel { DiscordViewModel(pref) }
		val navGraph = remember(navController) {
			navController.createGraph(startDestination = Home) {
				composable<Home> {
					GifList(
						lastGifViewModel.lastGifs.toList(),
						loadMore = { lastGifViewModel.nextPage() },
						navController
					)
				}
				composable<GifView> {
					val gifView: GifView = it.toRoute()
					val gif: Gif = Json.decodeFromString<Gif>(gifView.gif)
					GifViewScreen(gif, navController)
				}
				composable<Series> {
					SeriesScreen(navController)
				}
				composable<Seasons> {
					val seasons: Seasons = it.toRoute()
					SeasonsScreen(seasons.seriesName, navController)
				}
				composable<Episodes> {
					val episodes: Episodes = it.toRoute()
					EpisodesScreen(episodes.seriesName, episodes.seasonNumber, navController)
				}
				composable<EpisodeDetail> {
					val episodeDetail: EpisodeDetail = it.toRoute()
					EpisodeDetailScreen(episodeDetail.seriesName, episodeDetail.seasonNumber, episodeDetail.episodeNumber, navController, pref)
				}
				composable<MyGif> {
					GifList(
						myGifViewModel.myGif.toList(),
						loadMore = { myGifViewModel.nextPage() },
						navController
					)
				}
				composable<Search> {
					SearchScreen(navController)
				}
			}
		}
		val scope = rememberCoroutineScope()
		scope.launch {
			lastGifViewModel.fetch(0)
			myGifViewModel.fetch(0)
			pref.data.collect {
				if(it.contains(TOKEN)) {
					logged = true
				}
			}
		}
		Scaffold(
			topBar = { TopBar(navController) },
			bottomBar = { BottomBar(logged, navController, discordViewModel) {
				scope.launch {
					pref.updateData {
						it.toMutablePreferences().apply {
							remove(TOKEN)
						}
					}
				}
				logged = false
			}
			},
			contentWindowInsets = WindowInsets.statusBars
		) {
			Column(Modifier.fillMaxWidth().padding(it), horizontalAlignment = Alignment.CenterHorizontally) {
				NavHost(navController, navGraph)
			}
		}
	}
}