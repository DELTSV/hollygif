package fr.imacaron.mobile.gif.ui.page

import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EpisodeDetailScreen(seriesName: String, seasonNumber: Int, episodeName: String, episodeNumber: Int) {
	Column(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
		Card(Modifier.fillMaxWidth(0.6f)) {
			Text("Episode $episodeNumber:", Modifier.padding(8.dp).padding(bottom = 0.dp))
			Text(episodeName.replace('_', ' '), Modifier.padding(8.dp).padding(top = 0.dp))
		}
	}
}