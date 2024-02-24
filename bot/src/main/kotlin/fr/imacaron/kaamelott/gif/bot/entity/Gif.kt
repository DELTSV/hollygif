package fr.imacaron.kaamelott.gif.entity

import dev.kord.common.entity.Snowflake
import fr.imacaron.kaamelott.gif.repository.EpisodeEntity
import fr.imacaron.kaamelott.gif.repository.GifEntity
import fr.imacaron.kaamelott.gif.repository.SeasonEntity
import fr.imacaron.kaamelott.gif.repository.SeriesEntity
import kotlinx.datetime.Instant

class Gif(
	val entity: GifEntity
) {
	val user: Snowflake
		get() = Snowflake(entity.user)

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