package fr.imacaron.kaamelott.gif.entity

import fr.imacaron.kaamelott.gif.repository.EpisodeEntity
import fr.imacaron.kaamelott.gif.repository.EpisodeRepository
import fr.imacaron.kaamelott.gif.repository.SceneRepository

class Episode(
	private val sceneRepository: SceneRepository,
	private val entity: EpisodeEntity
) {
	val title: String
		get() = entity.title

	val fps: Int
		get() = entity.fps

	val width: Int
		get() = entity.width

	val height: Int
		get() = entity.height

	val duration: Double
		get() = entity.duration

	inner class SceneList {
		operator fun get(i: Int): Scene = sceneRepository.getEpisodeScene(entity, i).getOrThrow().let { Scene(it) }
	}
}