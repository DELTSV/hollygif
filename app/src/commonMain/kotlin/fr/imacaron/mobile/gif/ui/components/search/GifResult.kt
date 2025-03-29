package fr.imacaron.mobile.gif.ui.components.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import fr.imacaron.mobile.gif.Json
import fr.imacaron.mobile.gif.types.Gif
import fr.imacaron.mobile.gif.ui.GifView
import fr.imacaron.mobile.gif.ui.components.GifImage

fun LazyGridScope.GifResult(data: List<Gif>, navController: NavHostController, loadMore: () -> Unit) {
	data.forEachIndexed { index, gif ->
		if(index == data.size - 1) {
			loadMore()
		}
		item {
			GifImage("https://gif.imacaron.fr/api/gif/file/${gif.file}", Modifier.clickable(onClick = { navController.navigate(
				GifView(Json.encodeToString(gif))) }))
		}
	}
}