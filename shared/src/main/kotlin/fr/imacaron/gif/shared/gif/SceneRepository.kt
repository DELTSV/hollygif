package fr.imacaron.gif.shared.gif

import fr.imacaron.gif.shared.NotFoundException
import fr.imacaron.gif.shared.search.EpisodeEntity
import fr.imacaron.gif.shared.search.EpisodeTable
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.gte
import org.ktorm.dsl.lte
import org.ktorm.entity.Entity
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.double
import org.ktorm.schema.int

class SceneRepository(
	private val db: Database
) {
	fun getEpisodeScenes(episode: EpisodeEntity): Result<List<DbSceneEntity>> {
		TODO("Not yet implemented")
	}

	fun getEpisodeScene(episode: EpisodeEntity, index: Int): Result<DbSceneEntity> {
		return db.scenes.find { (SceneTable.episode eq episode.id) and (SceneTable.index eq index) }?.let {
			Result.success(it)
		} ?: Result.failure(NotFoundException("Cannot found scene"))
	}

	fun getEpisodeSceneAt(episode: EpisodeEntity, at: Double): Result<DbSceneEntity> {
		return db.scenes.find { (SceneTable.start lte at) and (SceneTable.end gte at) and (SceneTable.episode eq episode.id) }?.let {
			Result.success(it)
		} ?: Result.failure(NotFoundException("Cannot find scene with this timecode"))
	}

	fun addEpisodeScene(info: DbSceneEntity): Result<DbSceneEntity> {
		db.scenes.add(info)
		return Result.success(info)
	}
}

open class SceneTable(alias: String?): Table<DbSceneEntity>("SCENES", alias) {
	companion object: SceneTable(null)
	override fun aliased(alias: String) = SceneTable(alias)

	val id = int("id_scene").primaryKey().bindTo { it.id }
	val start = double("start").bindTo { it.start }
	val end = double("end").bindTo { it.end }
	val index = int("index").bindTo { it.index }
	val episode = int("episode").references(EpisodeTable) { it.episode }

	val episodes: EpisodeTable get() = episode.referenceTable as EpisodeTable
}

interface DbSceneEntity: Entity<DbSceneEntity> {
	var id: Int
	var start: Double
	var end: Double
	var index: Int
	var episode: EpisodeEntity

	companion object: Entity.Factory<DbSceneEntity>()
}

internal val Database.scenes get() = this.sequenceOf(SceneTable)