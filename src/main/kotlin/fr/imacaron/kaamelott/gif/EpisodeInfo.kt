package fr.imacaron.kaamelott.gif

data class EpisodeInfo(
    val sceneChange: List<Double>,
    val frameRate: Int,
    val width: Int,
    val height: Int,
    val duration: Double
)
