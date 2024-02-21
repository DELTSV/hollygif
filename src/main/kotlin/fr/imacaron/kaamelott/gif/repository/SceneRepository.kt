package fr.imacaron.kaamelott.gif.repository

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.double
import org.ktorm.schema.int

class SceneRepository(
	private val db: Database
) {
	fun getEpisodeScenes(episode: EpisodeEntity): Result<List<SceneEntity>> {
		TODO("Not yet implemented")
	}

	fun getEpisodeScene(episode: EpisodeEntity, index: Int): Result<SceneEntity> {
		TODO("Not yet implemented")
	}

	fun addEpisodeScene(info: SceneEntity): Result<SceneEntity> {
		db.scenes.add(info)
		return Result.success(info)
	}
}

object SceneTable: Table<SceneEntity>("SCENES") {
	val id = int("id_scene").primaryKey().bindTo { it.id }
	val start = double("start").bindTo { it.start }
	val end = double("end").bindTo { it.end }
	val index = int("index").bindTo { it.index }
	val episode = int("episode").references(EpisodeTable) { it.episode }
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