package fr.imacaron.gif.shared.repository

import fr.imacaron.gif.shared.NotFoundException
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.ktorm.schema.*

class EpisodeRepository(
	private val db: Database
) {
	fun getSeasonEpisodes(season: SeasonEntity, page: Int, pageSize: Int): Result<List<Pair<EpisodeEntity, Int>>> =
		Result.success(db.episodes.filter { EpisodeTable.season eq season.id }.sortedBy { it.number }.drop(page * pageSize).take(pageSize).map { it to getEpisodeGifTotal(it).getOrDefault(0) })

	fun getSeasonEpisodesCount(season: SeasonEntity): Result<Int> =
		Result.success(db.episodes.count { EpisodeTable.season eq season.id })

	fun getSeasonEpisode(season: SeasonEntity, number: Int): Result<EpisodeEntity> =
		db.episodes.find { (EpisodeTable.season eq season.id) and (EpisodeTable.number eq number) }?.let {
			Result.success(it)
		} ?: Result.failure(NotFoundException("Episode not found"))

	fun addEpisode(episode: EpisodeEntity): Result<EpisodeEntity> {
		db.episodes.add(episode)
		return Result.success(episode)
	}

	fun getEpisodeGifTotal(episode: EpisodeEntity): Result<Int> {
		return Result.success(db.gifs.filter { it.scenes.episode eq episode.id }.count())
	}

}

open class EpisodeTable(alias: String?): Table<EpisodeEntity>("EPISODES", alias) {
	companion object: EpisodeTable(null)
	override fun aliased(alias: String) = EpisodeTable(alias)

	val id = int("id_episode").primaryKey().bindTo { it.id }
	val number = int("number").bindTo { it.number }
	val fps = int("fps").bindTo { it.fps }
	val width = int("width").bindTo { it.width }
	val height = int("height").bindTo { it.height }
	val title = varchar("title").bindTo { it.title }
	val duration = double("duration").bindTo { it.duration }
	val season = int("season").references(SeasonTable) { it.season }
	val script = text("script").bindTo { it.script }

	val seasons: SeasonTable get() = season.referenceTable as SeasonTable
}

interface EpisodeEntity: Entity<EpisodeEntity> {
	val id: Int
	var number: Int
	var fps: Int
	var width: Int
	var height: Int
	var title: String
	var duration: Double
	var season: SeasonEntity
	var script: String

	companion object: Entity.Factory<EpisodeEntity>()
}

val Database.episodes get() = this.sequenceOf(EpisodeTable)