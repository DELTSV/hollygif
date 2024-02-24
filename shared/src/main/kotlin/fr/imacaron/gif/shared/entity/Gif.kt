package fr.imacaron.gif.shared.entity

import fr.imacaron.gif.shared.repository.EpisodeEntity
import fr.imacaron.gif.shared.repository.GifEntity
import fr.imacaron.gif.shared.repository.SeasonEntity
import fr.imacaron.gif.shared.repository.SeriesEntity
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
}