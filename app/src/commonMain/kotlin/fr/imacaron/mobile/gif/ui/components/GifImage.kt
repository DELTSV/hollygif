package fr.imacaron.mobile.gif.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun GifImage(url: String, modifier: Modifier = Modifier)