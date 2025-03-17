package fr.imacaron.mobile.gif.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
	var canNavigateBack by remember { mutableStateOf(false) }
	navController.addOnDestinationChangedListener { nav, _, _ ->
		canNavigateBack = nav.previousBackStackEntry != null
	}
	TopAppBar(
		title =  { Text("Kaamelott Gif") },
		navigationIcon = {
			if (canNavigateBack) {
				IconButton( onClick = { navController.navigateUp(); } ) {
					Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
				}
			}
		}
	)
}