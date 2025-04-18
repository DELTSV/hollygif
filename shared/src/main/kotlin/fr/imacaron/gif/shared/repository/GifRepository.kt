package fr.imacaron.gif.shared.repository

import fr.imacaron.gif.shared.NotFoundException
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
	fun getGif(id: Int): Result<Gif> {
		return db.gifs.find { it.id eq id }?.let {
			Result.success(Gif(it))
		} ?: Result.failure(NotFoundException("Gif not found"))
	}

	fun getUserGifs(user: String, page: Int, pageSize: Int): List<Gif> =
		db.gifs
			.filter { GifsTable.user eq user }
			.sortedBy { it.date.desc() }
			.drop(page * pageSize).take(pageSize)
			.map { Gif(it) }

	fun getSceneGifs(sceneEntity: SceneEntity, page: Int, pageSize: Int): List<Gif> =
		db.gifs.filter { GifsTable.scene eq sceneEntity.id }.drop(page * pageSize).take(pageSize).map { Gif(it) }

	fun getEpisodeGifs(episodeEntity: EpisodeEntity, page: Int, pageSize: Int): List<Gif> =
		db.gifs.filter { GifsTable.scenes.episode eq episodeEntity.id }.sortedBy { it.date.desc() }.drop(page * pageSize).take(pageSize).map { Gif(it) }

	fun getSeasonGifs(seasonEntity: SeasonEntity, page: Int, pageSize: Int): List<Gif> =
		db.gifs.filter { EpisodeTable.season eq seasonEntity.id }.drop(page * pageSize).take(pageSize).map { Gif(it) }

	fun getSeriesGifs(seriesEntity: SeriesEntity, page: Int, pageSize: Int): List<Gif> =
		db.gifs
			.filter { GifsTable.scenes.episodes.seasons.series eq seriesEntity.id }
			.sortedBy { it.date.desc() }
			.drop(page * pageSize).take(pageSize)
			.map { Gif(it) }

	fun addGif(gifEntity: GifEntity): GifEntity = gifEntity.apply { db.gifs.add(gifEntity) }

	fun searchGifsByText(search: String, page: Int, pageSize: Int): Pair<List<GifEntity>, Int> {
		return db.gifs.filter { it.text like search }.drop(page * pageSize).take(pageSize).map { it } to
				db.gifs.count { it.text like search }
	}

	fun keepGif(gif: Int) {
		db.gifs.find { it.id eq gif }?.let {
			it.keep = true
			it.flushChanges()
		}
	}

	fun deleteGif(gif: Int) {
		db.gifs.removeIf { it.id eq gif }
	}

	fun deleteOldGif() {
		db.gifs.filter { (it.date lt Instant.fromEpochMilliseconds(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3)) and !it.keep }.forEach {
			it.delete()
		}
	}
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
	val keep = boolean("keep").bindTo { it.keep }

	val scenes: SceneTable get() = scene.referenceTable!! as SceneTable
}

interface GifEntity : Entity<GifEntity> {
	val id: Int
	var user: String
	var timecode: String
	var text: String
	var date: Instant
	var scene: SceneEntity
	var status: GifStatus
	var keep: Boolean

	companion object: Entity.Factory<GifEntity>()
}

val Database.gifs get() = this.sequenceOf(GifsTable)