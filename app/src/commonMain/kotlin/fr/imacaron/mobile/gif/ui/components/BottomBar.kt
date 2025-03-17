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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.navigation.NavHostController
import com.final_class.webview_multiplatform_mobile.webview.WebViewPlatform
import com.final_class.webview_multiplatform_mobile.webview.controller.rememberWebViewController
import fr.imacaron.mobile.gif.ui.Home
import fr.imacaron.mobile.gif.ui.Series
import kaamelott_gif.app.generated.resources.Res
import kaamelott_gif.app.generated.resources.logo
import kaamelott_gif.app.generated.resources.outline_login
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BottomBar(isLogged: Boolean, navController: NavHostController) {
	val webViewController by rememberWebViewController()

	WebViewPlatform(webViewController = webViewController)
	webViewController.consume()
	BottomAppBar {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceAround,
			verticalAlignment = Alignment.CenterVertically
		) {
			if(isLogged) {
				Text("Mes gifs")
			}
			TextButton(onClick = { navController.navigate(Series) }) {
				Text("Les séries")
			}
			IconButton(onClick = { navController.popBackStack(Home, false) }) {
				Image(
					painterResource(Res.drawable.logo),
					contentDescription = "Logo",
					colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcAtop)
				)
			}
			if(isLogged) {
				Text("Moi")
				Text("Se déconnecter")
			} else {
				IconButton(
					onClick = {
						webViewController.open("https://discord.com/oauth2/authorize?client_id=1203063708264431689&scope=identify&redirect_uri=https://gif.imacaron.fr/callback&response_type=token")
					}
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