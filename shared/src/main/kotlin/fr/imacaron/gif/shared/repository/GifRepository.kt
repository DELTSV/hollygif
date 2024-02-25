package fr.imacaron.gif.shared.repository

import fr.imacaron.gif.shared.PAGE_SIZE
import fr.imacaron.gif.shared.entity.Gif
import fr.imacaron.gif.shared.enums.TEnum
import fr.imacaron.gif.shared.enums.enumInt
import kotlinx.datetime.Instant
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.schema.*

class GifRepository(
	private val db: Database
) {
	fun getUserGifs(user: String, page: Int): List<Gif> =
		db.gifs.filter { GifsTable.user eq user }.drop(page * PAGE_SIZE).take(PAGE_SIZE).map { Gif(it) }

	fun getSceneGifs(sceneEntity: SceneEntity, page: Int): List<Gif> =
		db.gifs.filter { GifsTable.scene eq sceneEntity.id }.drop(page * PAGE_SIZE).take(PAGE_SIZE).map { Gif(it) }

	fun getEpisodeGifs(episodeEntity: EpisodeEntity, page: Int): List<Gif> =
		db.gifs.filter { GifsTable.scenes.episode eq episodeEntity.id }.drop(page * PAGE_SIZE).take(PAGE_SIZE).map { Gif(it) }

	fun getSeasonGifs(seasonEntity: SeasonEntity, page: Int): List<Gif> =
		db.gifs.filter { EpisodeTable.season eq seasonEntity.id }.drop(page * PAGE_SIZE).take(PAGE_SIZE).map { Gif(it) }

	fun getSeriesGifs(seriesEntity: SeriesEntity, page: Int): List<Gif> =
		db.gifs
			.filter { SeasonTable.series eq seriesEntity.id }
			.drop(page * PAGE_SIZE).take(PAGE_SIZE)
			.map { Gif(it) }

	fun addGif(gifEntity: GifEntity): GifEntity = gifEntity.apply { db.gifs.add(gifEntity) }
}

enum class GifStatus(override val value: Int): TEnum<Int> {
	FAILED(-1),
	SUCCESS(1);

	override fun getValue(value: Int): TEnum<Int> = entries.first { it.value == value }
}

object GifsTable: Table<GifEntity>("GIFS") {
	val id = int("id_gif").primaryKey().bindTo { it.id }
	val user = varchar("user").bindTo { it.user }
	val timecode = varchar("timecode").bindTo { it.timecode }
	val text = text("text").bindTo { it.text }
	val date = datetime("date").bindTo { it.date }
	val scene = int("scene").references(SceneTable) { it.scene }
	val status = enumInt<GifStatus>("status").bindTo { it.status }

	val scenes: SceneTable get() = scene.referenceTable as SceneTable
}

interface GifEntity : Entity<GifEntity> {
	val id: Int
	var user: String
	var timecode: String
	var text: String
	var date: Instant
	var scene: SceneEntity
	var status: GifStatus

	companion object: Entity.Factory<GifEntity>()
}

val Database.gifs get() = this.sequenceOf(GifsTable)