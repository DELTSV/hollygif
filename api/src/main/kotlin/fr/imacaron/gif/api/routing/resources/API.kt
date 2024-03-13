package fr.imacaron.gif.api.routing.resources

import io.ktor.resources.*

@Resource("/api")
class API {
	@Resource("/gif")
	class Gif(val parent: API = API()) {
		@Resource("/file/{file}")
		class File(val gif: Gif = Gif(), val file: String)

		@Resource("/{id}")
		class ID(val gif: Gif = Gif(), val id: Int)

		@Resource("/me")
		class Me(val gif: Gif = Gif())
	}

	@Resource("/series")
	class Series(val parent: API = API()) {
		@Resource("/{name}")
		class Name(val parent: Series = Series(), val name: String) {
			@Resource("/seasons")
			class Seasons(val parent: Name) {
				@Resource("/{number}")
				class Number(val parent: Seasons, val number: Int) {
					@Resource("/episodes")
					class Episodes(val parent: Number) {
						@Resource("/{index}")
						class Index(val parent: Episodes, val index: Int)
					}
				}
			}
		}
	}
}