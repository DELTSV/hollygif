package fr.imacaron.gif.shared.infrastrucutre.repository

import fr.imacaron.gif.shared.NotFoundException
import fr.imacaron.gif.shared.gif.SceneRepository
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

class DbSceneRepository(
	private val db: Database
) : SceneRepository {
	override fun getEpisodeScenes(episode: EpisodeEntity): Result<List<SceneEntity>> {
		TODO("Not yet implemented")
	}

	override fun getEpisodeScene(episode: EpisodeEntity, index: Int): Result<SceneEntity> {
		return db.scenes.find { (SceneTable.episode eq episode.id) and (SceneTable.index eq index) }?.let {
			Result.success(it)
		} ?: Result.failure(NotFoundException("Cannot found scene"))
	}

	override fun getEpisodeSceneAt(episode: EpisodeEntity, at: Double): Result<SceneEntity> {
		return db.scenes.find { (SceneTable.start lte at) and (SceneTable.end gte at) and (SceneTable.episode eq episode.id) }?.let {
			Result.success(it)
		} ?: Result.failure(NotFoundException("Cannot find scene with this timecode"))
	}

	override fun addEpisodeScene(info: SceneEntity): Result<SceneEntity> {
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