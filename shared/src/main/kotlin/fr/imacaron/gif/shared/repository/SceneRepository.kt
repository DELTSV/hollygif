package fr.imacaron.gif.shared.repository

import fr.imacaron.gif.shared.NotFoundException
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.gte
import org.ktorm.dsl.lte
import org.ktorm.entity.Entity
import org.ktorm.entity.add
import org.ktorm.entity.count
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.map
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.double
import org.ktorm.schema.int

class SceneRepository(
	private val db: Database
) {
	fun getEpisodeScenes(episode: EpisodeEntity): Result<List<SceneEntity>> {
		return db.scenes.filter { it.episode eq episode.id }.map { it }.let {
			Result.success(it)
		}
	}

	fun getEpisodeScenesCount(episode: EpisodeEntity): Result<Int> {
		return db.scenes.count { it.episode eq episode.id }.let {
			Result.success(it)
		}
	}

	fun getEpisodeScene(episode: EpisodeEntity, index: Int): Result<SceneEntity> {
		return db.scenes.find { (SceneTable.episode eq episode.id) and (SceneTable.index eq index) }?.let {
			Result.success(it)
		} ?: Result.failure(NotFoundException("Cannot found scene"))
	}

	fun getEpisodeSceneAt(episode: EpisodeEntity, at: Double): Result<SceneEntity> {
		return db.scenes.find { (SceneTable.start lte at) and (SceneTable.end gte at) and (SceneTable.episode eq episode.id) }?.let {
			Result.success(it)
		} ?: Result.failure(NotFoundException("Cannot find scene with this timecode"))
	}

	fun addEpisodeScene(info: SceneEntity): Result<SceneEntity> {
		db.scenes.add(info)
		return Result.success(info)
	}
}

open class SceneTable(alias: String?): Table<SceneEntity>("SCENES", alias) {
	companion object: SceneTable(null)
	override fun aliased(alias: String) = SceneTable(alias)

	val id = int("id_scene").primaryKey().bindTo { it.id }
	val start = double("start").bindTo { it.start }
	val end = double("end").bindTo { it.end }
	val index = int("index").bindTo { it.index }
	val episode = int("episode").references(EpisodeTable) { it.episode }

	val episodes: EpisodeTable get() = episode.referenceTable as EpisodeTable
}

interface SceneEntity: Entity<SceneEntity> {
	var id: Int
	var start: Double
	var end: Double
	var index: Int
	var episode: EpisodeEntity

	companion object: Entity.Factory<SceneEntity>()
}

internal val Database.scenes get() = this.sequenceOf(SceneTable)