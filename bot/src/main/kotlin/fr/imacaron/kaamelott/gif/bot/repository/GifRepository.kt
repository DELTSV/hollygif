package fr.imacaron.kaamelott.gif.repository

import dev.kord.common.entity.Snowflake
import fr.imacaron.kaamelott.gif.PAGE_SIZE
import fr.imacaron.kaamelott.gif.entity.Gif
import kotlinx.datetime.Instant
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.schema.*

class GifRepository(
	private val db: Database
) {
	fun getUserGifs(user: Snowflake, page: Int): List<Gif> =
		db.gifs.filter { it.user eq user.toString() }.drop(page * PAGE_SIZE).take(PAGE_SIZE).map { Gif(it) }

	fun getSceneGifs(sceneEntity: SceneEntity, page: Int): List<Gif> =
		db.gifs.filter { it.scene eq sceneEntity.id }.drop(page * PAGE_SIZE).take(PAGE_SIZE).map { Gif(it) }

	fun getEpisodeGifs(episodeEntity: EpisodeEntity, page: Int): List<Gif> =
		db.gifs.filter { it.scenes.episode eq episodeEntity.id }.drop(page * PAGE_SIZE).take(PAGE_SIZE).map { Gif(it) }

	fun getSeasonGifs(seasonEntity: SeasonEntity, page: Int): List<Gif> =
		db.gifs.filter { it.scenes.episodes.season eq seasonEntity.id }.drop(page * PAGE_SIZE).take(PAGE_SIZE).map { Gif(it) }

	fun getSeriesGifs(seriesEntity: SeriesEntity, page: Int): List<Gif> =
		db.gifs
			.filter { it.scenes.episodes.seasons.series eq seriesEntity.id }
			.drop(page * PAGE_SIZE).take(PAGE_SIZE)
			.map { Gif(it) }

	fun addGif(gifEntity: GifEntity): GifEntity = gifEntity.apply { db.gifs.add(gifEntity) }
}

object GifsTable: Table<GifEntity>("GIFS") {
	val id = int("id_gif").primaryKey().bindTo { it.id }
	val user = varchar("user").bindTo { it.user }
	val timecode = varchar("timecode").bindTo { it.timecode }
	val text = text("text").bindTo { it.text }
	val date = datetime("date").bindTo { it.date }
	val scene = int("scene").references(SceneTable) { it.scene }

	val scenes: SceneTable get() = scene.referenceTable as SceneTable
}

interface GifEntity : Entity<GifEntity> {
	val id: Int
	var user: String
	var timecode: String
	var text: String
	var date: Instant
	var scene: SceneEntity

	companion object: Entity.Factory<GifEntity>()
}

val Database.gifs get() = this.sequenceOf(GifsTable)