package fr.imacaron.mobile.gif

import kotlinx.serialization.json.Json

val Json = Json {
	encodeDefaults = false
	ignoreUnknownKeys = true
}