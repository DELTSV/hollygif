package fr.imacaron.mobile.gif.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import kaamelott_gif.app.composeapp.generated.resources.Res
import kaamelott_gif.app.composeapp.generated.resources.outline_login
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BottomBar(isLogged: Boolean) {
	BottomAppBar {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceAround,
			verticalAlignment = Alignment.CenterVertically
		) {
			if(isLogged) {
				Text("Mes gifs")
			}
			TextButton(onClick = {}) {
				Text("Les séries")
			}
			TextButton(onClick = {}) {
				Text("K")
			}
			if(isLogged) {
				Text("Moi")
				Text("Se déconnecter")
			} else {
				IconButton(
					onClick = {}
				) {
					Image(
						painterResource(Res.drawable.outline_login),
						contentDescription = "Se connecter",
						colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcAtop)
					)
				}
			}
		}
	}
}