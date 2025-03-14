package fr.imacaron.mobile.gif.ui.components

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.network.cachecontrol.CacheControlCacheStrategy
import coil3.network.ktor3.KtorNetworkFetcherFactory

@OptIn(ExperimentalCoilApi::class)
@Composable
actual fun GifImage(url: String, modifier: Modifier) {
	val context = LocalContext.current
	val imageLoader = ImageLoader.Builder(context)
		.components {
			if (SDK_INT >= 28) {
				add(AnimatedImageDecoder.Factory())
			} else {
				add(GifDecoder.Factory())
			}
			this.add(
				KtorNetworkFetcherFactory(
				cacheStrategy = { CacheControlCacheStrategy() }
			))
		}
		.build()

	AsyncImage(
		url,
		null,
		imageLoader,
		modifier.aspectRatio(512f / 279f),
		contentScale = ContentScale.FillWidth
	)
}