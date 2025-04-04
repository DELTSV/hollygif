package fr.imacaron.mobile.gif.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import chaintech.videoplayer.host.MediaPlayerEvent
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import fr.imacaron.mobile.gif.types.Gif
import fr.imacaron.mobile.gif.types.Scene
import fr.imacaron.mobile.gif.types.SceneStatus
import fr.imacaron.mobile.gif.ui.theme.getKaamelottFont
import kaamelott_gif.app.generated.resources.Res
import kaamelott_gif.app.generated.resources.outline_chevron_left
import kaamelott_gif.app.generated.resources.outline_chevron_right
import kaamelott_gif.app.generated.resources.outline_pause
import kaamelott_gif.app.generated.resources.outline_play_arrow
import kaamelott_gif.app.generated.resources.outline_save
import kaamelott_gif.app.generated.resources.outline_text_fields
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlayerCard(scenes: List<Scene>, currentScene: Int, onSceneClick: (Scene) -> Unit, createGif: (text: String) -> Unit, status: String) {
	if (scenes.isNotEmpty()) {
		val scene = remember { scenes[currentScene] }
		val density = LocalDensity.current
		var height by remember { mutableStateOf(0.dp) }
		var width by remember { mutableStateOf(0.dp) }
		var pause by remember { mutableStateOf(false) }
		val url = "https://gif.imacaron.fr/api/series/${scene.episode.season.series.name}/seasons/${scene.episode.season.number}/episodes/${scene.episode.number}/scenes/$currentScene/file"
		var text by remember { mutableStateOf("") }
		var textDialog by remember { mutableStateOf(false) }
		val host = remember {
			MediaPlayerHost(
				mediaUrl = url,
				isLooping = false
			)
		}
		LaunchedEffect(url) {
			host.loadUrl(url)
		}
		var time by remember { mutableStateOf(0) }
		host.onEvent = { event ->
			when(event) {
				is MediaPlayerEvent.PauseChange -> {
					pause = event.isPaused
				}
				is MediaPlayerEvent.CurrentTimeChange -> {
					time = event.currentTime
				}
				else -> Unit
			}
		}
		Card(Modifier.height(height).onGloballyPositioned {
			width = with(density) { it.size.width.toDp() }
			height = (scene.episode.height * width.value / scene.episode.width).dp
		}) {
			Box(Modifier.fillMaxSize()) {
				VideoPlayerComposable(
					modifier = Modifier.fillMaxSize(),
					playerHost = host,
					playerConfig = VideoPlayerConfig(
						showControls = false,
						isFastForwardBackwardEnabled = false,
						isZoomEnabled = false,
						isDurationVisible = false,
						isSeekBarVisible = false,
						isFullScreenEnabled = false,
						isScreenLockEnabled = false,
						isScreenResizeEnabled = false,
						enableResumePlayback = false
					)
				)
				IconButton(
					{
						onSceneClick(scenes[currentScene - 1])
						if(pause) {
							host.play()
						}
					},
					Modifier.align(Alignment.CenterStart)
				) {
					Image(painterResource(Res.drawable.outline_chevron_left), "Scène précédente", colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcAtop))
				}
				IconButton(
					{
						onSceneClick(scenes[currentScene + 1])
						if(pause) {
							host.play()
						}
					},
					Modifier.align(Alignment.CenterEnd)
				) {
					Image(painterResource(Res.drawable.outline_chevron_right), "Scène suivante", colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcAtop))
				}
				IconButton({ host.togglePlayPause() }, Modifier.align(Alignment.Center)) {
					if(pause) {
						Image(painterResource(Res.drawable.outline_play_arrow), "Lecture", colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcAtop))
					} else {
						Image(painterResource(Res.drawable.outline_pause), "Pause", colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcAtop))
					}
				}
				Column(Modifier.align(Alignment.BottomCenter)) {
					Text(text, Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontFamily = getKaamelottFont(), fontSize = MaterialTheme.typography.displaySmall.fontSize)
					LazyRow(Modifier.fillMaxWidth().height(16.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
						scenes.forEachIndexed { index, scene ->
							item {
								val duration = scene.end - scene.start
								val color = if (index < currentScene) Color.White else Color.White.copy(alpha = 0.5f)
								val boxWidth = (duration * width.value / scene.episode.duration) * 3
								Box(Modifier.fillMaxHeight().width(boxWidth.dp).background(color).clickable {
									onSceneClick(scene)
									if(pause) {
										host.play()
									}
								}) {
									if(index == currentScene) {
										Box(Modifier.fillMaxHeight().background(Color.White).width((time * boxWidth / duration).dp))
									}
								}
							}
						}
					}
				}
				Row(Modifier.align(Alignment.TopStart), verticalAlignment = Alignment.CenterVertically) {
					IconButton({ textDialog = true }) {
						Image(
							painterResource(Res.drawable.outline_text_fields),
							"Ajouter du texte",
							colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcAtop)
						)
					}
					IconButton({
						createGif(text)
					}) {
						Image(
							painterResource(Res.drawable.outline_save),
							"Sauvegarder le gif",
							colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcAtop)
						)
					}
					Text(status)
				}
			}
			if(textDialog) {
				Dialog(
					{ textDialog = false }
				) {
					Card{
						Column(Modifier.padding(24.dp)) {
							Text("Texte du gif", Modifier.padding(bottom = 16.dp), style = MaterialTheme.typography.headlineSmall)
							OutlinedTextField(text, { text = it })
							Row(Modifier.padding(top = 24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.End) {
								TextButton({ textDialog = false }) {
									Text("Enregistrer")
								}
							}
						}
					}
				}
			}
		}
	}
}