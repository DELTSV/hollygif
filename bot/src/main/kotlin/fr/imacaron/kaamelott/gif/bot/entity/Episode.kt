package fr.imacaron.kaamelott.gif.entity

import fr.imacaron.kaamelott.gif.NotFoundException
import fr.imacaron.kaamelott.gif.repository.*

class Episode(
	private val sceneRepository: SceneRepository,
	val entity: EpisodeEntity,
	val season: SeasonEntity
) {
	val title: String
		get() = entity.title

	val fps: Int
		get() = entity.fps

	val width: Int
		get() = entity.width

	val height: Int
		get() = entity.height

	val number: Int
		get() = entity.number

	val duration: Double
		get() = entity.duration

	val scenes = SceneList()

	inner class SceneList {
		operator fun get(i: Int): Scene = sceneRepository.getEpisodeScene(entity, i).getOrThrow().let { Scene(it, entity) }

		fun getSceneFromTime(time: Double): Scene? {
			return sceneRepository.getEpisodeSceneAt(entity, time).getOrElse {
				if(it is NotFoundException) {
					return null
				}
				throw Exception()
			}
		}
	}
}