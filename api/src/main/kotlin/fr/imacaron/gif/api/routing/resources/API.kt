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
		class Name(val series: Series = Series(), val name: String) {
			@Resource("/seasons")
			class Seasons(val seriesName: Name) {
				@Resource("/{number}")
				class Number(val seasons: Seasons, val number: Int) {
					@Resource("/episodes")
					class Episodes(val seasonNumber: Number) {
						@Resource("/{index}")
						class Index(val episodes: Episodes, val index: Int) {
							@Resource("/transcriptions")
							class Transcriptions(val episodeIndex: Index)
						}
					}
				}
			}
		}
	}
}