package fr.imacaron.gif.shared.gif

import fr.imacaron.gif.shared.infrastrucutre.repository.DbSceneEntity
import fr.imacaron.gif.shared.infrastrucutre.repository.EpisodeEntity

interface SceneRepository {
    fun getEpisodeScenes(episode: EpisodeEntity): Result<List<DbSceneEntity>>
    fun getEpisodeScene(episode: EpisodeEntity, index: Int): Result<DbSceneEntity>
    fun getEpisodeSceneAt(episode: EpisodeEntity, at: Double): Result<DbSceneEntity>
    fun addEpisodeScene(info: DbSceneEntity): Result<DbSceneEntity>
}