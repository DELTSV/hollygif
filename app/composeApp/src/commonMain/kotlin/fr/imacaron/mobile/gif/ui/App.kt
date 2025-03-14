package fr.imacaron.mobile.gif.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import fr.imacaron.mobile.gif.ui.components.BottomBar
import fr.imacaron.mobile.gif.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    AppTheme {
        var logged by remember { mutableStateOf(false) }
        Scaffold(
            bottomBar = { BottomBar(logged) }
        ) {

        }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { logged = !logged }) {
                Text("Click me!")
            }
        }
    }
}