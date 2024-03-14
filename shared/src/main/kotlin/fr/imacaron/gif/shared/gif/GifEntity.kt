package fr.imacaron.gif.shared.gif

import fr.imacaron.gif.shared.search.EpisodeEntity
import fr.imacaron.gif.shared.search.SeasonEntity
import fr.imacaron.gif.shared.search.SeriesEntity
import kotlinx.datetime.Instant

class GifEntity(
	val entity: DbGifEntity
) {
	val user: String
		get() = entity.user

	val text: String
		get() = entity.text

	val timeCode: String
		get() = entity.timecode

	val date: Instant
		get() = entity.date

	val episode: EpisodeEntity
		get() = entity.scene.episode

	val season: SeasonEntity
		get() = entity.scene.episode.season

	val series: SeriesEntity
		get() = entity.scene.episode.season.series

	val status: GifStatus
		get() = entity.status
}