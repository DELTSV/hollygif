package fr.imacaron.gif.api.usecases.gif

data class GifCreation(
	val series: String,
	val season: Int,
	val episode: Int,
	val timecode: String,
	val text: String
)
