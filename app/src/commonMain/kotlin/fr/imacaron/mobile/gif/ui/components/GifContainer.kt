package fr.imacaron.mobile.gif.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import fr.imacaron.mobile.gif.ui.theme.onBackgroundDark

@Composable
fun GifContainer(gif: Gif, navController: NavHostController) {
	Box {
		GifImage(
			"https://gif.imacaron.fr/api/gif/file/${gif.file}",
			Modifier.clip(RoundedCornerShape(8.dp)).clickable {
				navController.navigate(GifView(Json.encodeToString(gif)))
			})
		Row(
			Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()
				.alpha(0.8f),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(
				8.dp,
				Alignment.Start
			)
		) {
			Text(gif.creator.globalName ?: "", style = MaterialTheme.typography.titleSmall, color = onBackgroundDark)
			if (gif.creator.avatar != null) {
				AsyncImage(
					model = gif.creator.avatar,
					null,
					Modifier.clip(CircleShape).width(16.dp)
				)
			}
		}
	}
}