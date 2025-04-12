package fr.imacaron.mobile.gif.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import fr.imacaron.mobile.gif.types.Gif
import fr.imacaron.mobile.gif.ui.EpisodeDetail
import fr.imacaron.mobile.gif.ui.Episodes
import fr.imacaron.mobile.gif.ui.components.GifImage
import fr.imacaron.mobile.gif.viewmodel.DiscordViewModel
import fr.imacaron.mobile.gif.viewmodel.LastGifViewModel
import fr.imacaron.mobile.gif.viewmodel.MyGifViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
fun GifViewScreen(gif: Gif, navController: NavController, discordViewModel: DiscordViewModel, myGifViewModel: MyGifViewModel, gifViewModel: LastGifViewModel) {
	Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
		Card {
			GifImage("https://gif.imacaron.fr/api/gif/file/${gif.file}", Modifier.fillMaxWidth())
			Row (Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				AsyncImage(gif.creator.avatar, null, Modifier.clip(CircleShape))
				Text(gif.creator.globalName ?: "")
			}
		}
		Card(Modifier.fillMaxWidth()) {
			Column(Modifier.padding(8.dp)) {
				Text("Créateur: ${gif.creator.globalName}")
				Text("Date: ${gif.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).date.format(LocalDate.Format {
					byUnicodePattern("dd/MM/yyyy")
				})}")
				Text("Timecode: ${gif.timecode}")
				Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
					TextButton(modifier = Modifier.padding(2.dp), onClick = { navController.navigate(Episodes(gif.scene.episode.season.series.name, gif.scene.episode.season.number)) }) {
						Text("Livre: ${gif.scene.episode.season.number}")
					}
					TextButton(modifier = Modifier.padding(2.dp), onClick = {
						navController.navigate(EpisodeDetail(
							gif.scene.episode.season.series.name,
							gif.scene.episode.season.number,
							gif.scene.episode.title,
							gif.scene.episode.number
						))
					}) {
						Text("Épisode: ${gif.scene.episode.number} ${gif.scene.episode.title}")
					}
				}
			}
		}
		if(gif.creator.id == discordViewModel.user?.id) {
			Card(Modifier.fillMaxWidth()) {
				Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End) {
					Button({
						myGifViewModel.viewModelScope.launch {
							if(myGifViewModel.delete(gif.id)) {
								gifViewModel.delete(gif.id)
								navController.popBackStack()
							}
						}
					}) {
						Text("Supprimer")
					}
				}
			}
		}
	}
}