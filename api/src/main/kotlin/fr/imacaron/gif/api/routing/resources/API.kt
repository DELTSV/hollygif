package fr.imacaron.gif.api.routing.resources

import io.ktor.resources.*

@Resource("/api")
class API {
	@Resource("/gif")
	class Gif(val parent: API = API()) {
		@Resource("{file}")
		class File(val gif: Gif = Gif(), val file: String)
	}
}