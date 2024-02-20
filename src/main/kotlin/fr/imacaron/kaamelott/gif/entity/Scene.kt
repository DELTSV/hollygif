package fr.imacaron.kaamelott.gif.entity

import fr.imacaron.kaamelott.gif.repository.SceneEntity

class Scene(
	private val entity: SceneEntity
) {
	val start
		get() = entity.start

	val end
		get() = entity.end

	val index
		get() = entity.index
}