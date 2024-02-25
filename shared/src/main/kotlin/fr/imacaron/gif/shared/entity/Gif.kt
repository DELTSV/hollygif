package fr.imacaron.gif.shared.entity

import fr.imacaron.gif.shared.repository.*
import kotlinx.datetime.Instant

class Gif(
	val entity: GifEntity
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