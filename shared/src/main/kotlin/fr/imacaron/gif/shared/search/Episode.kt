package fr.imacaron.gif.shared.search

import fr.imacaron.gif.shared.NotFoundException
import fr.imacaron.gif.shared.gif.SceneEntity
import fr.imacaron.gif.shared.infrastrucutre.repository.EpisodeEntity
import fr.imacaron.gif.shared.infrastrucutre.repository.DbSceneRepository

class Episode(
		private val sceneRepository: DbSceneRepository,
		val entity: EpisodeEntity,
		val season: Season
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

	val script: String
		get() = entity.script

	inner class SceneList {
		operator fun get(i: Int): SceneEntity = sceneRepository.getEpisodeScene(entity, i).getOrThrow().let { SceneEntity(it, this@Episode) }

		fun getSceneFromTime(time: Double): SceneEntity? {
			return sceneRepository.getEpisodeSceneAt(entity, time).getOrElse {
				if(it is NotFoundException) {
					return null
				}
				throw Exception()
			}.let { SceneEntity(it, this@Episode) }
		}
	}
}