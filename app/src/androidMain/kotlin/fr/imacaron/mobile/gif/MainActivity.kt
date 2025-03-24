package fr.imacaron.mobile.gif

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fr.imacaron.mobile.gif.ui.App
import kotlinx.coroutines.runBlocking

val pref = createDataStore()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextUtils.setContext(this)
        intent?.let {
            if(it.action == Intent.ACTION_VIEW) {
                it.data?.let { data ->
                    if(data.host == "app.gif.imacaron.fr") {
                        data.toString().split("#").getOrNull(1)?.split("&")?.associate {
                            val (k, v) = it.split("=")
                            k to v
                        }?.let { params ->
                            if(params.contains("scope") && params["scope"] == "identify") {
                                params["access_token"]?.let { accessToken ->
                                    runBlocking {
                                        pref.updateData {
                                            it.toMutablePreferences().apply {
                                                this[TOKEN] = accessToken
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        setContent {
            App(pref)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}