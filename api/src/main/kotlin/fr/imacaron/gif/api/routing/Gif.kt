package fr.imacaron.gif.api.routing

import io.ktor.resources.*

@Resource("/gif")
class Gif(val parent: API = API()) {
	@Resource("{file}")
	class File(val gif: Gif = Gif(), val file: String)
}