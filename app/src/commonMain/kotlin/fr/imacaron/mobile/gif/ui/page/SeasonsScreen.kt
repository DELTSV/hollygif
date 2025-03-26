package fr.imacaron.mobile.gif.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import fr.imacaron.mobile.gif.ui.Episodes
import fr.imacaron.mobile.gif.viewmodel.SeasonsViewModel
import kotlinx.coroutines.launch

@Composable
fun SeasonsScreen(seriesName: String, navController: NavHostController) {
	val viewModel = viewModel<SeasonsViewModel> {
		SeasonsViewModel(seriesName)
	}
	val scope = rememberCoroutineScope()
	scope.launch {
		viewModel.fetch(0)
	}
	LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
		viewModel.seasons.forEach { season ->
			item {
				Card(onClick = { navController.navigate(Episodes(seriesName, season.number)) }) {
					AsyncImage(season.series.logo, null, Modifier.fillMaxWidth(), colorFilter = ColorFilter.tint(Color.White, BlendMode.SrcOut))
					Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.Center) {
						Text("Saison ${season.number}", textAlign = TextAlign.Center, style = MaterialTheme.typography.titleLarge)
					}
				}
			}
		}
	}
}