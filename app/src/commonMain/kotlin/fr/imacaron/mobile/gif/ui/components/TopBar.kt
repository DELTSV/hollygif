package fr.imacaron.mobile.gif.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavController
import fr.imacaron.mobile.gif.ui.Search
import kaamelott_gif.app.generated.resources.Res
import kaamelott_gif.app.generated.resources.outline_open_in_new
import kaamelott_gif.app.generated.resources.outline_search
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
	var canNavigateBack by remember { mutableStateOf(false) }
	var search by remember { mutableStateOf(false) }
	val uriHandler = LocalUriHandler.current
	navController.addOnDestinationChangedListener { nav, dest, _ ->
		canNavigateBack = nav.previousBackStackEntry != null
		search = dest.route == Search::class.qualifiedName
	}
	TopAppBar(
		title =  { Text("Kaamelott Gif") },
		navigationIcon = {
			if (canNavigateBack) {
				IconButton( onClick = { navController.navigateUp(); } ) {
					Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
				}
			}
		},
		actions = {
			if(!search) {
				IconButton(onClick = { navController.navigate(Search) }) {
					Image(
						painterResource(Res.drawable.outline_search),
						"Recherche",
						colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcAtop)
					)
				}
			}
			IconButton(onClick = { uriHandler.openUri("https://app.gif.imacaron.fr") }) {
				Image(
					painterResource(Res.drawable.outline_open_in_new),
					"Ouvrir le site",
					colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcAtop)
				)
			}
		}
	)
}