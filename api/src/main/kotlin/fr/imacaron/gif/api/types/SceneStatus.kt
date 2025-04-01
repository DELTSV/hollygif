package fr.imacaron.gif.api.types

import fr.imacaron.gif.shared.entity.Scene
import kotlinx.serialization.Serializable

@Serializable
data class SceneStatus(
	val scene: Boolean = false,
	val textLength: Boolean = false,
	val text: Boolean = false,
	val gif: Boolean = false,
	val gifId: Int? = null,
	val error: String? = null
) {
	constructor(data: Scene.Status, id: Int? = null): this(
		scene = data.scene,
		textLength = data.textLength,
		text = data.text,
		gif = data.gif,
		error = data.error?.message,
		gifId = id
	)
}
