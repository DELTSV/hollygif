package fr.imacaron.mobile.gif.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import com.final_class.webview_multiplatform_mobile.webview.WebViewPlatform
import com.final_class.webview_multiplatform_mobile.webview.controller.rememberWebViewController
import com.final_class.webview_multiplatform_mobile.webview.settings.android.AndroidWebViewModifier
import com.final_class.webview_multiplatform_mobile.webview.settings.android.instantAppsEnabled
import fr.imacaron.mobile.gif.ui.Home
import fr.imacaron.mobile.gif.ui.MyGif
import fr.imacaron.mobile.gif.ui.Series
import fr.imacaron.mobile.gif.viewmodel.DiscordViewModel
import kaamelott_gif.app.generated.resources.Res
import kaamelott_gif.app.generated.resources.logo
import kaamelott_gif.app.generated.resources.outline_login
import kaamelott_gif.app.generated.resources.outline_logout
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BottomBar(isLogged: Boolean, navController: NavHostController, discordViewModel: DiscordViewModel, onLogout: () -> Unit) {
	val webViewController by rememberWebViewController()
	val scope = rememberCoroutineScope()

	WebViewPlatform(webViewController = webViewController, androidSettings = AndroidWebViewModifier.instantAppsEnabled(true))
	webViewController.consume()
	webViewController.webViewState
	BottomAppBar {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceAround,
			verticalAlignment = Alignment.CenterVertically
		) {
			if(isLogged) {
				TextButton(onClick = {
					try {
						val myGif = navController.getBackStackEntry(MyGif)
						navController.popBackStack(myGif.toRoute<MyGif>(), false)
					} catch (_: Exception) {
						navController.navigate(MyGif)
					}
				} ) {
					Text("Mes gifs", style = MaterialTheme.typography.titleLarge)
				}
			}
			TextButton(onClick = {
				try {
					val series = navController.getBackStackEntry(Series)
					navController.popBackStack(series.toRoute<Series>(), false)
				} catch (_: Exception) {
					navController.navigate(Series)
				}
			}) {
				Text("Les séries", style = MaterialTheme.typography.titleLarge)
			}
			IconButton(onClick = { navController.popBackStack(Home, false) }) {
				Image(
					painterResource(Res.drawable.logo),
					contentDescription = "Logo",
					colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcAtop)
				)
			}
			if(isLogged) {
				if(discordViewModel.user == null) {
					scope.launch {
						discordViewModel.fetchUser()
					}
				}
				IconButton( onClick = {}) {
					AsyncImage(
						"https://cdn.discordapp.com/avatars/${discordViewModel.user?.id}/${discordViewModel.user?.avatar}.png",
						"Avatar de l'utilisateur ${discordViewModel.user?.username}",
						modifier = Modifier.clip(CircleShape)
					)
				}
				IconButton(
					onClick = onLogout
				) {
					Image(
						painterResource(Res.drawable.outline_logout),
						contentDescription = "Se déconnecter",
						colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcAtop)
					)
				}
			} else {
				IconButton(
					onClick = {
						webViewController.open("https://discord.com/oauth2/authorize?client_id=1203063708264431689&scope=identify&redirect_uri=https://app.gif.imacaron.fr/callback&response_type=token")
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