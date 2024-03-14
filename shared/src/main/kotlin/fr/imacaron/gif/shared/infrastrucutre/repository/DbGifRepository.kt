package fr.imacaron.gif.shared.infrastrucutre.repository

import fr.imacaron.gif.shared.NotFoundException
import fr.imacaron.gif.shared.gif.GifEntity
import fr.imacaron.gif.shared.gif.GifRepository
import fr.imacaron.gif.shared.infrastrucutre.enums.TEnum
import fr.imacaron.gif.shared.infrastrucutre.enums.enumInt
import kotlinx.datetime.Instant
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.schema.*

class DbGifRepository(
	private val db: Database
) : GifRepository {
	override fun getGif(id: Int): Result<GifEntity> {
		return db.gifs.find { GifsTable.id eq id }?.let {
			Result.success(GifEntity(it))
		} ?: Result.failure(NotFoundException("Gif not found"))
	}

	override fun getUserGifs(user: String, page: Int, pageSize: Int): List<GifEntity> =
		db.gifs
			.filter { GifsTable.user eq user }
			.sortedBy { GifsTable.date.desc() }
			.drop(page * pageSize).take(pageSize)
			.map { GifEntity(it) }

	override fun getSceneGifs(sceneEntity: DbSceneEntity, page: Int, pageSize: Int): List<GifEntity> =
		db.gifs.filter { GifsTable.scene eq sceneEntity.id }.drop(page * pageSize).take(pageSize).map { GifEntity(it) }

	override fun getEpisodeGifs(episodeEntity: EpisodeEntity, page: Int, pageSize: Int): List<GifEntity> =
		db.gifs.filter { GifsTable.scenes.episode eq episodeEntity.id }.drop(page * pageSize).take(pageSize).map { GifEntity(it) }

	override fun getSeasonGifs(seasonEntity: SeasonEntity, page: Int, pageSize: Int): List<GifEntity> =
		db.gifs.filter { EpisodeTable.season eq seasonEntity.id }.drop(page * pageSize).take(pageSize).map { GifEntity(it) }

	override fun getSeriesGifs(seriesEntity: SeriesEntity, page: Int, pageSize: Int): List<GifEntity> =
		db.gifs
			.filter { GifsTable.scenes.episodes.seasons.series eq seriesEntity.id }
			.sortedBy { GifsTable.date.desc() }
			.drop(page * pageSize).take(pageSize)
			.map { GifEntity(it) }

	override fun addGif(gifEntity: DbGifEntity): DbGifEntity = gifEntity.apply { db.gifs.add(gifEntity) }
}

enum class GifStatus(override val value: Int): TEnum<Int> {
	FAILED(-1),
	SUCCESS(1);

	override fun getValue(value: Int): TEnum<Int> = entries.first { it.value == value }
}

object GifsTable: Table<DbGifEntity>("GIFS") {
	val id = int("id_gif").primaryKey().bindTo { it.id }
	val user = varchar("user").bindTo { it.user }
	val timecode = varchar("timecode").bindTo { it.timecode }
	val text = text("text").bindTo { it.text }
	val date = datetime("date").bindTo { it.date }
	val scene = int("scene").references(SceneTable) { it.scene }
	val status = enumInt<GifStatus>("status").bindTo { it.status }

	val scenes: SceneTable get() = scene.referenceTable!! as SceneTable
}

interface DbGifEntity : Entity<DbGifEntity> {
	val id: Int
	var user: String
	var timecode: String
	var text: String
	var date: Instant
	var scene: DbSceneEntity
	var status: GifStatus

	companion object: Entity.Factory<DbGifEntity>()
}

val Database.gifs get() = this.sequenceOf(GifsTable)