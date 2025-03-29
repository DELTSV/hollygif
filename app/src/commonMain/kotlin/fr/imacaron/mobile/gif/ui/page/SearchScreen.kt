package fr.imacaron.mobile.gif.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import fr.imacaron.mobile.gif.types.Episode
import fr.imacaron.mobile.gif.types.Gif
import fr.imacaron.mobile.gif.types.Series
import fr.imacaron.mobile.gif.types.Transcription
import fr.imacaron.mobile.gif.ui.components.search.EpisodeResult
import fr.imacaron.mobile.gif.ui.components.search.GifResult
import fr.imacaron.mobile.gif.ui.components.search.TextResult
import fr.imacaron.mobile.gif.viewmodel.SearchType
import fr.imacaron.mobile.gif.viewmodel.SearchViewModel
import kaamelott_gif.app.generated.resources.Res
import kaamelott_gif.app.generated.resources.outline_search
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavHostController) {
	val searchViewModel = viewModel<SearchViewModel>()
	val scope = rememberCoroutineScope()
	val keyboardController = LocalSoftwareKeyboardController.current
	LaunchedEffect(searchViewModel.selectedType) {
		if(searchViewModel.searchText.isNotBlank()) {
			searchViewModel.search()
		}
	}
	Column {
		OutlinedTextField(
			searchViewModel.searchText,
			onValueChange = { searchViewModel.searchText = it },
			keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
			keyboardActions = KeyboardActions(onSearch = { scope.launch { searchViewModel.search() }; keyboardController?.hide() }),
			trailingIcon = {
				IconButton(onClick = { scope.launch { searchViewModel.search() }; keyboardController?.hide() }) {
					Image(
						painterResource(Res.drawable.outline_search),
						"Rechercher",
						colorFilter = ColorFilter.tint(Color.White, BlendMode.SrcAtop)
					)
				}
			}
		)
		PrimaryTabRow(searchViewModel.selectedType.index) {
			SearchType.entries.forEach { type ->
				Tab(selected = searchViewModel.selectedType == type, onClick = { searchViewModel.selectedType = type }, text = { Text(type.label) })
			}
		}
		LazyVerticalGrid(GridCells.Fixed(3), state = searchViewModel.gridState) {
			searchViewModel.result.forEach { type ->
				if (type.type != "series") {
					item(span = { GridItemSpan(3) }) {
						Row(
							Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainer),
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.SpaceBetween
						) {
							Text(typeToText(type.type))
							if(searchViewModel.selectedType.type == null) {
								TextButton(onClick = {
									SearchType.entries.find { it.type == type.type }?.let { searchViewModel.selectedType = it }
								}) {
									Text("Voir plus")
								}
							}
						}
					}
					when (type.data.firstOrNull()) {
						is Gif -> GifResult(type.data as List<Gif>, navController) { scope.launch { searchViewModel.loadMore() } }
						is Series -> Unit
						is Episode -> EpisodeResult(type.data as List<Episode>, navController) { scope.launch { searchViewModel.loadMore() } }
						is Transcription -> TextResult(type.data as List<Transcription>, navController) { scope.launch { searchViewModel.loadMore() } }
						null -> Unit
					}
				}
			}
		}
	}
}

fun typeToText(type: String) = when (type) {
	"gif" -> "Gifs"
	"series" -> "Séries"
	"episode" -> "Episodes"
	"transcription" -> "Textes"
	else -> ""
}