package fr.imacaron.mobile.gif.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import fr.imacaron.mobile.gif.ui.Seasons
import fr.imacaron.mobile.gif.viewmodel.SeriesViewModel
import kotlinx.coroutines.launch

@Composable
fun SeriesScreen(navController: NavHostController) {
	val seriesViewModel = viewModel<SeriesViewModel>()
	val scope = rememberCoroutineScope()
	scope.launch {
		seriesViewModel.fetch(0)
	}
	LazyColumn(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
		seriesViewModel.series.forEach { series ->
			item {
				Card(onClick = { navController.navigate(Seasons(series.name)) }) {
					AsyncImage(series.logo, null, colorFilter = ColorFilter.tint(Color.White, BlendMode.SrcOut))
					Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.Center) {
						Text(series.name.replaceFirstChar { it.uppercaseChar() }, textAlign = TextAlign.Center, style = MaterialTheme.typography.displaySmall)
					}
				}
			}
		}
	}
}